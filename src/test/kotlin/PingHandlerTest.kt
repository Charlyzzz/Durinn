import assertk.assert
import assertk.assertions.isEqualTo
import org.http4k.serverless.BootstrapAppLoader
import org.http4k.serverless.lambda.ApiGatewayProxyRequest
import org.http4k.serverless.lambda.LambdaFunction
import org.junit.Test

class PingHandlerTest {

    @Test
    fun `returns ping when called`() {
        val lambda = LambdaFunction(mapOf(BootstrapAppLoader.HTTP4K_BOOTSTRAP_CLASS to PingHandler::class.java.name))
        val response = lambda.handle(ApiGatewayProxyRequest().apply {
            httpMethod = "GET"
        })

        assert(response.body).isEqualTo("pong")
        assert(response.statusCode).isEqualTo(200)
    }
}