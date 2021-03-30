import org.http4k.core.HttpHandler
import org.http4k.serverless.AppLoader

object PingHandler : AppLoader {
    override fun invoke(env: Map<String, String>) = handlePing
}

object AuthorizationHandler : AppLoader {

    override fun invoke(env: Map<String, String>): HttpHandler {

        val dbURL = env["COUCH_URL"] ?: error("COUCH_URL is missing")

        val authorizer = AuthorizerWithAccessLogging(
            trusteeAuthorizer(finder = CouchTrusteeDeviceFinder(dbURL)),
            CouchAccessReporter(dbURL, RealClock)
        )

        return handleAuthorizationRequest(authorizer)
    }
}

