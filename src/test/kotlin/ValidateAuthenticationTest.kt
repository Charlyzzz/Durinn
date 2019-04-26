import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import org.junit.Test

class ValidateAuthenticationTest {

    @Test
    fun `returns true and the name when the UID exists`() {
        val trusteeIsPresent: TrusteeByDeviceIdFinder = { Trustee("some-uid", "jose") }
        val (estaAutorizado, nombre) = validateAuthentication(trusteeIsPresent, AuthorizationAttempt("some-uid"))
        assert(estaAutorizado).isTrue()
        assert(nombre).isEqualTo("jose")
    }

    @Test
    fun `returns false when the UID does not exists`() {
        val noTrusteeFound: TrusteeByDeviceIdFinder = { null }
        val (estaAutorizado, nombre) = validateAuthentication(noTrusteeFound, AuthorizationAttempt("other-uid"))
        assert(estaAutorizado).isFalse()
        assert(nombre).isNull()
    }
}
