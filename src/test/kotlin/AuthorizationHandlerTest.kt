import assertk.Assert
import assertk.assert
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import assertk.assertions.support.fail
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.core.ContentType
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.format.Jackson.asJsonObject
import org.http4k.serverless.lambda.ApiGatewayProxyResponse
import org.junit.Test

class AuthorizationHandlerTest {

    @Test
    fun `returns 400 with no body`() {
        val lambda = AuthorizationHandler(trusteeFinder = { TODO() })
        val response = lambda()
        assert(response.body).isEmpty()
        assert(response.statusCode).isEqualTo(400)
    }

    @Test
    fun `returns 400 when body is empty`() {
        val lambda = AuthorizationHandler(trusteeFinder = { TODO() })
        val response = lambda {
            body = "{}"
        }
        val errors = jacksonObjectMapper().readValue<Errors>(response.body.asJsonObject()["errors"].toString())
        assert(errors).contains("uid", listOf("is required"))
        assert(response.statusCode).isEqualTo(400)
    }

    @Test
    fun `returns 400 when device id is blank`() {
        val lambda = AuthorizationHandler(trusteeFinder = { TODO() })
        val response = lambda {
            body = """{"uid": ""}"""
        }
        val errors = jacksonObjectMapper().readValue<Errors>(response.body.asJsonObject()["errors"].toString())
        assert(errors).contains("uid", listOf("must not be blank"))
        assert(response.statusCode).isEqualTo(400)
    }

    @Test
    fun `returns 200 and result when device id is found`() {
        val returnJoseWhenIdMatch: TrusteeByDeviceIdFinder = {
            when (it!!) {
                "a77a1" -> Trustee(it, "jose")
                else -> null
            }
        }
        val lambda = AuthorizationHandler(trusteeFinder = returnJoseWhenIdMatch)
        val response = lambda {
            body = """{"uid": "a77a1"}"""
        }
        val (isAuthorized, name) = jacksonObjectMapper().readValue<AuthenticationResult>(response.body)
        assert(isAuthorized).isTrue()
        assert(name).isEqualTo("jose")
        assert(response.statusCode).isEqualTo(200)
        assert(response).hasContentType(APPLICATION_JSON)
    }

}

private fun Assert<ApiGatewayProxyResponse>.hasContentType(contentType: ContentType) {
    val actualContentType = actual.headers["content-type"]
    val expectedContentType = contentType.value
    if (actualContentType == null || !actualContentType.contains(expectedContentType))
        fail(expectedContentType, actualContentType)
}
