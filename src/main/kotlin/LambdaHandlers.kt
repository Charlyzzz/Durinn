import io.konform.validation.Invalid
import io.konform.validation.Valid
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.auto
import org.http4k.format.Jackson.json

class PingHandler : ApiGatewayProxyHandler() {
    override fun invoke(env: Map<String, String>): HttpHandler = {
        Response(OK).body("pong")
    }
}

class AuthorizationHandler(
    private val trusteeFinder: TrusteeByDeviceIdFinder = DynamoDbTrusteeByDeviceIdFinder(),
    env: Map<String, String> = System.getenv()
) : ApiGatewayProxyHandler(env) {


    override fun invoke(env: Map<String, String>): HttpHandler = ServerFilters.CatchLensFailure.then {
        val newAuthorizationAttempt = newAuthorizationLens(it)
        when (val validationResult = AuthorizationAttempt.validate(newAuthorizationAttempt)) {
            is Valid -> {
                Response(OK).with(
                    authorizationResultLens of validateAuthentication(trusteeFinder, newAuthorizationAttempt)
                )
            }
            is Invalid -> Response(Status.BAD_REQUEST).with(
                Body.json().toLens() of mapOf("errors" to validationResult.errors()).asJsonObject()
            )
        }
    }

    companion object {
        private val newAuthorizationLens = Body.auto<AuthorizationAttempt>().toLens()
        val authorizationResultLens = Body.auto<AuthenticationResult>().toLens()
    }
}