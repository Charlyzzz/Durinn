import org.http4k.serverless.BootstrapAppLoader
import org.http4k.serverless.lambda.ApiGatewayProxyRequest
import org.http4k.serverless.lambda.ApiGatewayProxyResponse
import org.http4k.serverless.lambda.LambdaFunction

/**
 * Allows to instantiate lambdas easier
 */
interface HttpLambda {
    companion object {
        inline operator fun <reified T> invoke(): LambdaFunction =
            LambdaFunction(mapOf(BootstrapAppLoader.HTTP4K_BOOTSTRAP_CLASS to T::class.java.name))
    }
}

/**
 * Allows to write simpler tests by calling the lambda with a nicer API :)
 */
operator fun LambdaFunction.invoke(transformation: ApiGatewayProxyRequest.() -> Unit = {}): ApiGatewayProxyResponse {
    val proxyRequest = ApiGatewayProxyRequest().apply {
        httpMethod = "GET"
        path = ""
    }.apply(transformation)
    return handle(proxyRequest)
}

typealias Errors = Map<String, List<String>>
