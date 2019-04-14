import io.konform.validation.Invalid
import io.konform.validation.Valid
import io.konform.validation.Validation
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.auto
import org.http4k.format.Jackson.json
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer

typealias Autorizados = Map<String, List<String>>

fun main() {

    val autorizados = mapOf(
        "erwin" to listOf("54:11:48:88", "04:67:72:b2:8f:48:80")
    )
    val nuevaAutorizacionLens = Body.auto<NuevaAutorizacion>().toLens()
    val respuestaDeAutorizacion = Body.auto<RespuestaDeAutorizacion>().toLens()

    val app = routes(
        "ping" bind GET to { Response(OK).body("pong") },
        "autorizar" bind POST to { request ->

            val nuevaAutorizacion = nuevaAutorizacionLens(request)
            val validationResult = NuevaAutorizacion.validator(nuevaAutorizacion)

            when (validationResult) {
                is Valid -> Response(OK).with(
                    respuestaDeAutorizacion of validar(nuevaAutorizacion, autorizados)
                )
                is Invalid -> Response(BAD_REQUEST).with(
                    Body.json().toLens() of mapOf("errors" to validationResult.errors()).asJsonObject()
                )
            }
        }
    )
    app.asServer(Jetty(9001)).start()
}

fun validar(nuevaAutorizacion: NuevaAutorizacion, autorizados: Autorizados): RespuestaDeAutorizacion {
    val autorizado = autorizados.entries.find { it.value.contains(nuevaAutorizacion.uid) }
    return autorizado?.let { RespuestaDeAutorizacion(estaAutorizado = true, nombre = it.key) }
        ?: RespuestaDeAutorizacion(estaAutorizado = false)
}

data class RespuestaDeAutorizacion(val estaAutorizado: Boolean, val nombre: String? = null)

data class NuevaAutorizacion(val uid: String?) {
    companion object {
        val validator = Validation<NuevaAutorizacion> {
            NuevaAutorizacion::uid required {
                notBlank()
            }
        }
    }
}
