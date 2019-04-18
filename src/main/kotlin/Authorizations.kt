import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.*
import io.konform.validation.Validation

typealias AuthorizedUsers = Map<String, List<String>>
typealias Validator = (String?) -> AuthenticationResult

fun validate(authorizationAttempt: AuthorizationAttempt, authorizedUsers: AuthorizedUsers): AuthenticationResult {
    val authorizedUser = authorizedUsers.entries.find { it.value.contains(authorizationAttempt.uid) }
    return authorizedUser?.let { AuthenticationResult(isAuthorized = true, name = it.key) }
        ?: AuthenticationResult(isAuthorized = false)
}

data class AuthenticationResult(val isAuthorized: Boolean, val name: String? = null)

data class AuthorizationAttempt(val uid: String?) {
    companion object {
        val validate = Validation<AuthorizationAttempt> {
            AuthorizationAttempt::uid required {
                notBlank()
            }
        }
    }
}

data class Trustee(
    val deviceId: String,
    val name: String
)

@DynamoDBTable(tableName = "durinn-trustees")
class TrusteeDocument {
    @DynamoDBHashKey(attributeName = "DeviceId")
    lateinit var deviceId: String

    @DynamoDBHashKey(attributeName = "Name")
    lateinit var name: String
}

fun TrusteeDocument.toModel() = Trustee(deviceId, name)