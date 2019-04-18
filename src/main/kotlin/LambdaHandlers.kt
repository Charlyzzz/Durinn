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
    private val validate: Validator

    init {
        val client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build()

        val mapper: DynamoDBTableMapper<TrusteeDocument, String, Any> =
            DynamoDBMapper(client).newTableMapper(TrusteeDocument::class.java)

        validate = { attempt ->
            val foundTrustee = mapper.load(attempt.uid)?.toModel()
            foundTrustee?.let {
                AuthenticationResult(isAuthorized = true, name = it.name)
            } ?: AuthenticationResult(isAuthorized = false)
        }
    }

    override fun invoke(env: Map<String, String>): HttpHandler = ServerFilters.CatchLensFailure.then {
        val newAuthorizationAttempt = newAuthorizationLens(it)
        when (val validationResult = AuthorizationAttempt.validate(newAuthorizationAttempt)) {
            is Valid -> {
                Response(OK).with(
                    authorizationResultLens of validate(newAuthorizationAttempt)
                )
            }
            is Invalid -> Response(Status.BAD_REQUEST).with(
                Body.json().toLens() of mapOf("errors" to validationResult.errors()).asJsonObject()
            )
        }
    }
}