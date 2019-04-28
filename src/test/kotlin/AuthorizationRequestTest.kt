import assertk.assert
import assertk.assertions.contains
import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasContentType
import org.http4k.hamkrest.hasStatus
import org.junit.Test

class HandleAuthorizationAttemptTest {

    @Test
    fun `returns 400 with no body`() {
        val handler = handleAuthorizationRequest { TODO() }
        val response = handler(Request())

        assertThat(response, hasStatus(BAD_REQUEST))
        assertThat(response, hasEmptyBody)
    }

    @Test
    fun `returns 400 when body is empty`() {
        val handler = handleAuthorizationRequest { TODO() }
        val response = handler(Request().body("{}"))

        val errors = errorsLens(response).getValue("errors")

        assert(errors).contains("uid", listOf("is required"))
        assertThat(response, hasStatus(BAD_REQUEST) and hasContentType(APPLICATION_JSON))
    }

    @Test
    fun `returns 400 when device id is blank`() {
        val handler = handleAuthorizationRequest { TODO() }
        val response = handler(Request().body("""{"uid": ""}"""))

        val errors = errorsLens(response).getValue("errors")

        assert(errors).contains("uid", listOf("must not be blank"))
        assertThat(response, hasStatus(BAD_REQUEST) and hasContentType(APPLICATION_JSON))
    }

    @Test
    fun `returns 200 and result when device id is found`() {
        val authorizer = trusteeAuthorizer {
            when (it) {
                "a77a1" -> Trustee(it, "jose")
                else -> null
            }
        }
        val handler = handleAuthorizationRequest(authorizer)
        val response = handler(Request().body("""{"uid": "a77a1"}"""))

        val expectedResult = AuthorizationResult(authorized = true, name = "jose")

        assertThat(response, hasBody(authenticationResultLens, equalTo(expectedResult)))
        assertThat(response, hasStatus(OK) and hasContentType(APPLICATION_JSON))
    }

    @Test
    fun `can be warmed up`() {
        val handler = handleAuthorizationRequest { TODO() }
        val response = handler(WarmUpRequest)
        assertThat(response, isFromWarmUp)
    }
}

val errorsLens = Body.auto<Errors>().toLens()
val authenticationResultLens = Body.auto<AuthorizationResult>().toLens()

fun main() {
    bench { Body.auto<AuthorizationResult>().toLens() }
    bench { ApacheClient() }
}

fun bench(block: () -> Unit) {
    val times = mutableListOf<Long>()
    repeat(10000) {
        val i = System.currentTimeMillis()
        block()
        val f = System.currentTimeMillis()
        times.add(f - i)
    }
    println("A: ${times.average()}, M: ${times.max()}, m: ${times.min()}")
}