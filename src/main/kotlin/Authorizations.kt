import io.konform.validation.Validation
import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.format.Jackson.auto

data class AuthenticationResult(val authorized: Boolean, val name: String? = null)

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

data class AuthorizationAttempt(val deviceId: String)

class CouchDbTrusteeByDeviceIdFinder : TrusteeByDeviceIdFinder {

    private val httpClient = ApacheClient()
    private val queryURI = "http://52.13.54.86:5984/trustees/_design/trustees/_view/byDevice"
    private val queryResultLens = Body.auto<TrusteeByDeviceViewResult>().toLens()

    override fun invoke(deviceId: String): Trustee? {
        val request = Request(Method.GET, queryURI + quotingQuery("key", deviceId))
        val response = httpClient(request)
        val queryResults = queryResultLens(response).rows
        if (queryResults.size > 1) throw RuntimeException("""Multiple devices registered with DeviceId: $deviceId""")
        return queryResults.firstOrNull()?.let { Trustee(it.deviceId, it.name) }
    }
}

typealias TrusteeByDeviceIdFinder = (String) -> Trustee?

fun validateAuthentication(
    findByDeviceId: TrusteeByDeviceIdFinder,
    authorizationAttempt: AuthorizationAttempt
): AuthenticationResult {
    return findByDeviceId(authorizationAttempt.deviceId)?.let {
        AuthenticationResult(authorized = true, name = it.name)
    } ?: AuthenticationResult(authorized = false)
}

data class Trustee(
    val deviceId: String,
    val name: String
)
