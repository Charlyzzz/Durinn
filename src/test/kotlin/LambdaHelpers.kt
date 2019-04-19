import org.http4k.serverless.lambda.ApiGatewayProxyRequest
import org.http4k.serverless.lambda.ApiGatewayProxyResponse

/**
 * Allows to write simpler tests by calling the lambda with a nicer API :)
 */
operator fun ApiGatewayProxyHandler.invoke(transformation: ApiGatewayProxyRequest.() -> Unit = {}): ApiGatewayProxyResponse {
    val proxyRequest = ApiGatewayProxyRequest().apply {
        httpMethod = "GET"
        path = ""
    }.apply(transformation)
    return handle(proxyRequest)
}