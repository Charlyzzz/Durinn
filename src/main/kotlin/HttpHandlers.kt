import io.konform.validation.Invalid
import io.konform.validation.Valid
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.filter.ServerFilters
import org.http4k.format.Gson.asJsonObject
import org.http4k.format.Gson.auto
import org.http4k.format.Gson.json
import org.http4k.lens.Header.CONTENT_TYPE

fun handleAuthorizationRequest(authorizer: Authorizer) = WarmUpFilter { println("Lambda warmed")} .then(ServerFilters.CatchLensFailure).then {
    println("pre lenses")
    val newAuthorizationLens = Body.auto<AuthorizationRequest>().toLens()
    val authorizationResultLens = Body.auto<AuthorizationResult>().toLens()
    println("post lenses")
    println("pre serialization 1")
    val authorizationRequest = newAuthorizationLens(it)
    println("post serialization 1")
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
