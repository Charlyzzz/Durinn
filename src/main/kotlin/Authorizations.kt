import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import io.konform.validation.Validation

data class AuthenticationResult(val authorized: Boolean, val name: String? = null)

data class AuthorizationAttempt(val uid: String?) {
    companion object {
        val validate = Validation<AuthorizationAttempt> {
            AuthorizationAttempt::uid required {
                notBlank()
            }
        }
    }
}

class DynamoDbTrusteeByDeviceIdFinder : TrusteeByDeviceIdFinder {

    private val dbMapper: DynamoDBMapper

    init {
        val client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build()
        dbMapper = DynamoDBMapper(client)
    }

    override fun invoke(deviceId: String?): Trustee? {
        return dbMapper.load(TrusteeDocument::class.java, deviceId)?.toModel()
    }
}

typealias TrusteeByDeviceIdFinder = (String?) -> Trustee?

fun validateAuthentication(
    findByDeviceId: TrusteeByDeviceIdFinder,
    authorizationAttempt: AuthorizationAttempt
): AuthenticationResult {
    return findByDeviceId(authorizationAttempt.uid)?.let {
        AuthenticationResult(authorized = true, name = it.name)
    } ?: AuthenticationResult(authorized = false)
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