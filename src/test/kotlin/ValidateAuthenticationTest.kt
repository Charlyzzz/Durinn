import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import org.junit.Test

class ValidateTest {

    @Test
    fun `returns true and the name when the UID exists`() {
        val autorizados = mapOf(
            "jose" to listOf("some-uid")
        )
        val (estaAutorizado, nombre) = validator(AuthorizationAttempt("some-uid"), autorizados)
        assert(estaAutorizado).isTrue()
        assert(nombre).isEqualTo("jose")
    }

    @Test
    fun `returns false when the UID does not exists`() {
        val autorizados = mapOf(
            "jose" to listOf("some-uid")
        )
        val (estaAutorizado, nombre) = validator(AuthorizationAttempt("other-uid"), autorizados)
        assert(estaAutorizado).isFalse()
        assert(nombre).isNull()
    }
}