import am.ik.yavi.core.ConstraintViolations
import am.ik.yavi.core.Validator
import am.ik.yavi.core.constraint
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
            val validationResult = NuevaAutorizacion.validator.validate(nuevaAutorizacion)

            if (validationResult.isValid) {
                Response(OK).with(
                    respuestaDeAutorizacion of validar(nuevaAutorizacion, autorizados)
                )
            } else {
                Response(BAD_REQUEST).with(
                    Body.json().toLens() of mapOf("errors" to validationResult.validationMessages()).asJsonObject()
                )
            }
        }
    )
    app.asServer(Jetty(9001)).start()
}

private fun ConstraintViolations.validationMessages() = violations().map { it.message() }

fun validar(nuevaAutorizacion: NuevaAutorizacion, autorizados: Autorizados): RespuestaDeAutorizacion {
    val autorizado = autorizados.entries.find { it.value.contains(nuevaAutorizacion.uid) }
    return autorizado?.let { RespuestaDeAutorizacion(estaAutorizado = true, nombre = it.key) }
        ?: RespuestaDeAutorizacion(estaAutorizado = false)
}

data class RespuestaDeAutorizacion(val estaAutorizado: Boolean, val nombre: String? = null)

data class NuevaAutorizacion(val uid: String?) {
    companion object {
        val validator: Validator<NuevaAutorizacion> = Validator.builder<NuevaAutorizacion>()
            .constraint(NuevaAutorizacion::uid) { notNull() }
            .build()
    }
}