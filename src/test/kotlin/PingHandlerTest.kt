import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.Status.Companion.OK
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.junit.Test

class PingHandlerTest {

    @Test
    fun `returns ping when called`() {
        val response = handlePing(Request())
        assertThat(response, hasStatus(OK) and hasBody("pong"))
    }

    @Test
    fun `can be warmed up`() {
        val response = handlePing(WarmUpRequest)
        assertThat(response, isFromWarmUp)
    }
}
