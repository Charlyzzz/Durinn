import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.then
import org.http4k.hamkrest.hasStatus
import org.junit.Test

class WarmUpFilterTest {

    private val handler: HttpHandler = { Response(INTERNAL_SERVER_ERROR) }

    @Test
    fun `returns warm up response and skips the next filters if header is present`() {
        val chain = WarmUpFilter.then(handler)
        val response = chain(WarmUpRequest)
        assertThat(response, isFromWarmUp)
    }

    @Test
    fun `continues with the chain if header is not set`() {
        val chain = WarmUpFilter.then(handler)
        val response = chain(Request())
        assertThat(response, hasStatus(INTERNAL_SERVER_ERROR))
    }
}
