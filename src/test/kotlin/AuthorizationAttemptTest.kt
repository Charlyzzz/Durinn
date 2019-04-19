import assertk.assert
import assertk.assertions.contains
import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasContentType
import org.http4k.hamkrest.hasStatus
import org.junit.Test


class AuthorizationAttemptTest {

    @Test
    fun `returns 400 with no body`() {
        val lambda = handleAuthorizationAttempt { TODO() }
        val response = lambda(Request())

        assertThat(response, hasStatus(BAD_REQUEST))
        assertThat(response, hasEmptyBody)
    }

    @Test
    fun `returns 400 when body is empty`() {
        val lambda = handleAuthorizationAttempt { TODO() }
        val response = lambda(Request().body("{}"))

        val errors = errorsLens(response).getValue("errors")

        assert(errors).contains("uid", listOf("is required"))
        assertThat(response, hasStatus(BAD_REQUEST) and hasContentType(APPLICATION_JSON))
    }


    @Test
    fun `returns 400 when device id is blank`() {
        val lambda = handleAuthorizationAttempt { TODO() }
        val response = lambda(Request().body("""{"uid": ""}"""))

        val errors = errorsLens(response).getValue("errors")

        assert(errors).contains("uid", listOf("must not be blank"))
        assertThat(response, hasStatus(BAD_REQUEST) and hasContentType(APPLICATION_JSON))
    }

    @Test
    fun `returns 200 and result when device id is found`() {
        val returnJoseWhenIdMatch: TrusteeByDeviceIdFinder = {
            when (it!!) {
                "a77a1" -> Trustee(it, "jose")
                else -> null
            }
        }
        val lambda = handleAuthorizationAttempt(returnJoseWhenIdMatch)
        val response = lambda(Request().body("""{"uid": "a77a1"}"""))

        val expectedResult = AuthenticationResult(authorized = true, name = "jose")

        assertThat(response, hasBody(authenticationResultLens, equalTo(expectedResult)))
        assertThat(response, hasStatus(OK) and hasContentType(APPLICATION_JSON))
    }
}

val errorsLens = Body.auto<Errors>().toLens()
val authenticationResultLens = Body.auto<AuthenticationResult>().toLens()
