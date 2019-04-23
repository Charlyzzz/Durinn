import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import java.time.ZonedDateTime

typealias AccessReporter = (AccessAttempt) -> Unit

class CouchDbAccessReporter : AccessReporter {

    private val httpClient = ApacheClient()
    private val insertURI = "http://52.13.54.86:5984/access"
    private val accessAttemptLens = Body.auto<AccessAttempt>().toLens()

    override fun invoke(accessAttempt: AccessAttempt) {
        val request = Request(POST, insertURI).with(
            accessAttemptLens of accessAttempt
        )
        httpClient(request)
    }
}

data class AccessAttempt(
    val deviceId: String,
    val authorized: Boolean,
    val timestamp: ZonedDateTime,
    val name: String?
)