import org.http4k.serverless.AppLoader

object PingHandler : AppLoader {
    override fun invoke(env: Map<String, String>) = handlePing
}

object AuthorizationHandler : AppLoader {
    private val authorizer = AuthorizerWithAccessLogging(
        trusteeAuthorizer(finder = CouchTrusteeDeviceFinder()),
        CouchAccessReporter(RealClock)
    )

    override fun invoke(env: Map<String, String>) = handleAuthorizationRequest(authorizer)
}

