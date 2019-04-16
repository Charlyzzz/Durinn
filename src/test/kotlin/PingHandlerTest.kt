import assertk.assert
import assertk.assertions.isEqualTo
import org.junit.Test

class PingHandlerTest {

    @Test
    fun `returns ping when called`() {
        val lambda = HttpLambda<PingHandler>()
        val response = lambda()
        assert(response.body).isEqualTo("pong")
        assert(response.statusCode).isEqualTo(200)
    }
}