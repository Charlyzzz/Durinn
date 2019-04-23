import org.http4k.serverless.AppLoader

object PingHandler : AppLoader {
    override fun invoke(env: Map<String, String>) = handlePing
}

object AuthorizationHandler : AppLoader {
    private val byIdFinder: TrusteeByDeviceIdFinder = CouchDbTrusteeByDeviceIdFinder()
    private val accessReporter: AccessReporter = CouchDbAccessReporter()

    override fun invoke(env: Map<String, String>) = handleAuthorizationRequest(byIdFinder, accessReporter)
}
