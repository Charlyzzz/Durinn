import org.http4k.core.*
import org.http4k.serverless.lambda.ApiGatewayProxyRequest
import org.http4k.serverless.lambda.ApiGatewayProxyResponse

abstract class ApiGatewayProxyHandler(
    private val env: Map<String, String> = System.getenv()
) {
    abstract fun invoke(env: Map<String, String>): HttpHandler

    fun handle(request: ApiGatewayProxyRequest) = invoke(env)(request.asHttp4k()).asApiGateway()
}

internal fun Response.asApiGateway() = ApiGatewayProxyResponse(status.code, headers.toMap(), bodyString())

internal fun ApiGatewayProxyRequest.asHttp4k() = (headers ?: emptyMap()).toList().fold(
    Request(Method.valueOf(httpMethod), uri())
        .body(body?.let(::MemoryBody) ?: Body.EMPTY)) { memo, (first, second) ->
    memo.header(first, second)
}

internal fun ApiGatewayProxyRequest.uri() = Uri.of(path ?: "").query((queryStringParameters
    ?: emptyMap()).toList().toUrlFormEncoded())
