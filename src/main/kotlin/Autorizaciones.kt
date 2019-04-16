import io.konform.validation.Validation

typealias Autorizados = Map<String, List<String>>

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
