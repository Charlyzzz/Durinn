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

    private val nuevaAutorizacionLens = Body.auto<NuevaAutorizacion>().toLens()
    private val respuestaDeAutorizacion = Body.auto<RespuestaDeAutorizacion>().toLens()
    private val autorizados = mapOf(
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
        val nuevaAutorizacion = nuevaAutorizacionLens(it)
        when (val validationResult = NuevaAutorizacion.validator(nuevaAutorizacion)) {
            is Valid -> Response(OK).with(
                respuestaDeAutorizacion of validar(nuevaAutorizacion, autorizados)
            )
            is Invalid -> Response(Status.BAD_REQUEST).with(
                Body.json().toLens() of mapOf("errors" to validationResult.errors()).asJsonObject()
            )
        }
    }
}