import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import org.junit.Test

class TrusteeAuthorizerTest {

    @Test
    fun `returns true and the name when the finder succeed`() {
        val trusteeIsPresent: DeviceFinder = { Trustee("some-uid", "jose") }
        val authorizer = trusteeAuthorizer(trusteeIsPresent)
        val (estaAutorizado, nombre) = authorizer(AuthorizationAttempt("some-uid"))
        assert(estaAutorizado).isTrue()
        assert(nombre).isEqualTo("jose")
    }

    @Test
    fun `returns false when the UID does not exists`() {
        val noTrusteeFound: DeviceFinder = { null }
        val authorizer = trusteeAuthorizer(noTrusteeFound)
        val (estaAutorizado, nombre) = authorizer(AuthorizationAttempt("other-uid"))
        assert(estaAutorizado).isFalse()
        assert(nombre).isNull()
    }
}
