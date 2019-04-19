import org.http4k.serverless.AppLoader

object PingHandler : AppLoader {
    override fun invoke(env: Map<String, String>) = handlePing
}

object AuthorizationHandler : AppLoader {
    private val deviceIdFinderInDynamo: TrusteeByDeviceIdFinder

    init {
        deviceIdFinderInDynamo = DynamoDbTrusteeByDeviceIdFinder()
    }

    override fun invoke(env: Map<String, String>) = handleAuthorizationAttempt(deviceIdFinderInDynamo)
}