import io.konform.validation.Invalid
import io.konform.validation.Valid
import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.auto
import org.http4k.format.Jackson.json
import java.time.ZoneId
import java.time.ZonedDateTime

fun handleAuthorizationRequest(
    findDeviceById: TrusteeByDeviceIdFinder,
    accessReporter: AccessReporter
) = WarmUpFilter.then(ServerFilters.CatchLensFailure).then {
    val newAuthorizationLens = Body.auto<AuthorizationRequest>().toLens()
    val authorizationResultLens = Body.auto<AuthenticationResult>().toLens()
    val authorizationRequest = newAuthorizationLens(it)

    when (val validationResult = AuthorizationRequest.validate(authorizationRequest)) {
        is Valid -> {
            val authorizationAttempt = authorizationRequest.toModel()
            val authenticationResult = validateAuthentication(findDeviceById, authorizationAttempt)
            val accessAttempt = AccessAttempt(
                deviceId = authorizationAttempt.deviceId,
                authorized = authenticationResult.authorized,
                timestamp = ZonedDateTime.now(ZoneId.of("UTC-3")),
                name = authenticationResult.name
            )
            accessReporter(accessAttempt)
            Response(Status.OK).with(
                authorizationResultLens of authenticationResult
            )
        }
        is Invalid -> Response(Status.BAD_REQUEST).with(
            Body.json().toLens() of mapOf("errors" to validationResult.errors()).asJsonObject()
        )
    }
}

val handlePing: HttpHandler = WarmUpFilter.then {
    Response(Status.OK).body("pong")
}
