import org.http4k.core.HttpHandler
import org.http4k.serverless.AppLoader

object PingHandler : AppLoader {
    override fun invoke(env: Map<String, String>) = handlePing
}

object AuthorizationHandler : AppLoader {

    init {
        println("Init")
    }

    private val authorizer = AuthorizerWithAccessLogging(
        trusteeAuthorizer(finder = CouchTrusteeDeviceFinder()),
        CouchAccessReporter(RealClock)
    )

    override fun invoke(env: Map<String, String>): HttpHandler {
        println("Pre invoke")
        val response = handleAuthorizationRequest(authorizer)
        println("Post invoke")
        return response
    }
}

