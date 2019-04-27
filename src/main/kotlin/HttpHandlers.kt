import io.konform.validation.Invalid
import io.konform.validation.Valid
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.auto
import org.http4k.format.Jackson.json
import org.http4k.lens.Header.CONTENT_TYPE

fun handleAuthorizationRequest(authorizer: Authorizer) = WarmUpFilter.then(ServerFilters.CatchLensFailure).then {
    val newAuthorizationLens = Body.auto<AuthorizationRequest>().toLens()
    val authorizationResultLens = Body.auto<AuthorizationResult>().toLens()
    val authorizationRequest = newAuthorizationLens(it)

    when (val validationResult = AuthorizationRequest.validate(authorizationRequest)) {
        is Valid -> {
            val authorizationAttempt = authorizationRequest.toModel()
            Response(Status.OK).with(
                authorizationResultLens of authorizer(authorizationAttempt)
            )
        }
        is Invalid -> Response(Status.BAD_REQUEST).with(
            Body.json().toLens() of mapOf("errors" to validationResult.errors()).asJsonObject()
        )
    }
}

val handlePing: HttpHandler = WarmUpFilter.then {
    Response(Status.OK).body("pong").with(CONTENT_TYPE of TEXT_PLAIN)
}
