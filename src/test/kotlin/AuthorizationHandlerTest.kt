import assertk.assert
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.format.Jackson.asJsonObject
import org.junit.Test

class AuthorizationHandlerTest {

    @Test
    fun `returns 400 with no body`() {
        val lambda = HttpLambda<AuthorizationHandler>()
        val response = lambda()
        assert(response.body).isEmpty()
        assert(response.statusCode).isEqualTo(400)
    }

    @Test
    fun `returns 400 when body is empty`() {
        val lambda = HttpLambda<AuthorizationHandler>()
        val response = lambda {
            body = "{}"
        }
        val errors = jacksonObjectMapper().readValue<Errors>(response.body.asJsonObject()["errors"].toString())
        assert(errors).contains("uid", listOf("is required"))
        assert(response.statusCode).isEqualTo(400)
    }

    @Test
    fun `returns 400 when uid is blank`() {
        val lambda = HttpLambda<AuthorizationHandler>()
        val response = lambda {
            body = """{"uid": ""}"""
        }
        val errors = jacksonObjectMapper().readValue<Errors>(response.body.asJsonObject()["errors"].toString())
        assert(errors).contains("uid", listOf("must not be blank"))
        assert(response.statusCode).isEqualTo(400)
    }
}
