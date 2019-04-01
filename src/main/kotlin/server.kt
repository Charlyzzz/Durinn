import com.fasterxml.jackson.databind.DeserializationFeature.*
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.format.ConfigurableJackson
import org.http4k.format.Jackson.auto
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer

object Jackson2 : ConfigurableJackson(
    KotlinModule()
        .asConfigurable()
        .withStandardMappings()
        .done()
        .disableDefaultTyping()
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(USE_BIG_DECIMAL_FOR_FLOATS, true)
        .configure(USE_BIG_INTEGER_FOR_INTS, true)
        .configure(FAIL_ON_NULL_FOR_PRIMITIVES, true)
)

fun main() {

    val autorizador = Autorizador()

    val app = routes(
        "ping" bind GET to { Response(OK).body("pong") },
        "autorizar" bind POST to { request ->
            val bodyLens: BiDiBodyLens<Foo> = Body.auto<Foo>().toLens()
            Response(OK).with(
                bodyLens of bodyLens(request)
            )
        }
    )
    app.asServer(Jetty(9000)).start()
}

data class Foo(val edad: Int?)