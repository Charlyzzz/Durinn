import io.konform.validation.Invalid
import io.konform.validation.Valid
import org.http4k.core.*
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.auto
import org.http4k.format.Jackson.json

fun handleAuthorizationAttempt(findDeviceById: TrusteeByDeviceIdFinder) = ServerFilters.CatchLensFailure.then {
    val newAuthorizationLens = Body.auto<AuthorizationAttempt>().toLens()
    val authorizationResultLens = Body.auto<AuthenticationResult>().toLens()
    val newAuthorizationAttempt = newAuthorizationLens(it)

    when (val validationResult = AuthorizationAttempt.validate(newAuthorizationAttempt)) {
        is Valid -> {
            Response(Status.OK).with(
                authorizationResultLens of validateAuthentication(findDeviceById, newAuthorizationAttempt)
            )
        }
        is Invalid -> Response(Status.BAD_REQUEST).with(
            Body.json().toLens() of mapOf("errors" to validationResult.errors()).asJsonObject()
        )
    }
}

val handlePing: HttpHandler = {
    Response(Status.OK).body("pong")
}