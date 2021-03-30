import io.konform.validation.Validation
import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.format.Gson.auto

data class AuthorizationRequest(val uid: String?) {
    companion object {
        val validate = Validation<AuthorizationRequest> {
            AuthorizationRequest::uid required {
                notBlank()
            }
        }
    }
}

fun AuthorizationRequest.toModel() = AuthorizationAttempt(uid!!)

data class AuthorizationResult(val authorized: Boolean, val name: String? = null)

data class AuthorizationAttempt(val deviceId: String)

class AuthorizerWithAccessLogging(
    private val authorizationFinder: Authorizer,
    private val accessReporter: AccessReporter
) : Authorizer {
    override fun invoke(authorizationAttempt: AuthorizationAttempt): AuthorizationResult {
        val authorizationResult = authorizationFinder(authorizationAttempt)
        accessReporter(authorizationAttempt, authorizationResult)
        return authorizationResult
    }
}

typealias DeviceFinder = (String) -> Trustee?

class CouchTrusteeDeviceFinder(val dbURL: String) : DeviceFinder {

    private val httpClient = ApacheClient()
    private val queryURI = "$dbURL/trustees/_design/trustees/_view/byDevice"
    private val queryResultLens = Body.auto<TrusteeByDeviceViewResult>().toLens()

    override fun invoke(deviceId: String): Trustee? {
        val request = Request(Method.GET, queryURI + quotingQuery("key", deviceId))
        val response = httpClient(request)
        val queryResults = queryResultLens(response).rows
        if (queryResults.size > 1) throw RuntimeException("""Multiple devices registered with DeviceId: $deviceId""")
        return queryResults.firstOrNull()?.let { Trustee(it.deviceId, it.name) }
    }
}

typealias Authorizer = (AuthorizationAttempt) -> AuthorizationResult

fun trusteeAuthorizer(finder: DeviceFinder): Authorizer = { (deviceId) ->
    finder(deviceId)?.let { AuthorizationResult(authorized = true, name = it.name) }
        ?: AuthorizationResult(authorized = false)
}

data class Trustee(
    val deviceId: String,
    val name: String
)
