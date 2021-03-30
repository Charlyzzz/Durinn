import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.format.Gson.auto
import java.time.ZonedDateTime

typealias AccessReporter = (AuthorizationAttempt, AuthorizationResult) -> Unit

class CouchAccessReporter(private val dbURL: String, private val clock: Clock) : AccessReporter {

    private val httpClient = ApacheClient()
    private val insertURI = "$dbURL/access"
    private val accessAttemptLens = Body.auto<AccessAttempt>().toLens()

    override fun invoke(authorizationAttempt: AuthorizationAttempt, authorizationResult: AuthorizationResult) {
        val accessAttempt = AccessAttempt(
            deviceId = authorizationAttempt.deviceId,
            authorized = authorizationResult.authorized,
            timestamp = clock.now(),
            name = authorizationResult.name
        )
        val request = Request(Method.POST, insertURI).with(
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
