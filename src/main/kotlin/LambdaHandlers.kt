import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTableMapper
import io.konform.validation.Invalid
import io.konform.validation.Valid
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.auto
import org.http4k.format.Jackson.json
import org.http4k.serverless.AppLoader

object PingHandler : AppLoader {
    override fun invoke(env: Map<String, String>): HttpHandler = {
        Response(OK).body("pong")
    }
}

object AuthorizationHandler : AppLoader {

    private val newAuthorizationLens = Body.auto<AuthorizationAttempt>().toLens()
    private val authorizationResultLens = Body.auto<AuthenticationResult>().toLens()
    private val validator: Validator

    init {
        val client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build()

        val mapper: DynamoDBTableMapper<TrusteeDocument, String, Any> =
            DynamoDBMapper(client).newTableMapper(TrusteeDocument::class.java)

        validator = { deviceId ->
            val foundTrustee: Trustee? = mapper.load(deviceId)?.toModel()
            foundTrustee?.let {
                AuthenticationResult(isAuthorized = true, name = it.name)
            } ?: AuthenticationResult(isAuthorized = false)
        }
    }

    val authorizedUsers = mapOf(
        "erwin" to listOf("54:11:48:88", "04:67:72:b2:8f:48:80"),
        "joel" to listOf("d2:07:c4:b8"),
        "eze" to listOf("a0:c6:eb:49"),
        "pabloB" to listOf("0:0b:91:79"),
        "seryo" to listOf("04:50:47:8a:55:5a:80"),
        "gastonT" to listOf("42:8f:f0:dc"),
        "lucas" to listOf("0f:1e:09:52"),
        "ailu" to listOf("8f:c2:9f:39")
    )

    override fun invoke(env: Map<String, String>): HttpHandler = ServerFilters.CatchLensFailure.then {
        val newAuthorizationAttempt = newAuthorizationLens(it)
        when (val validationResult = AuthorizationAttempt.validate(newAuthorizationAttempt)) {
            is Valid -> {
                println(validator(newAuthorizationAttempt.uid))
                Response(OK).with(
                    authorizationResultLens of validate(newAuthorizationAttempt, authorizedUsers)
                )
            }
            is Invalid -> Response(Status.BAD_REQUEST).with(
                Body.json().toLens() of mapOf("errors" to validationResult.errors()).asJsonObject()
            )
        }
    }
}