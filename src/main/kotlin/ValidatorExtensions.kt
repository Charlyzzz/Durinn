import io.konform.validation.Constraint
import io.konform.validation.Invalid
import io.konform.validation.ValidationBuilder

fun ValidationBuilder<String>.notBlank(): Constraint<String> {
    return addConstraint(
        "must not be blank"
    ) { it.isBlank().not() }
}

@Suppress("UNCHECKED_CAST")
fun <T> Invalid<T>.errors(nameInterpolator: String = "."): Map<String, List<String>> {
    val errorsField = javaClass.getDeclaredField("errors")
    errorsField.isAccessible = true
    val failedValidations = errorsField.get(this) as Map<List<String>, List<String>>
    return failedValidations.mapKeysTo(mutableMapOf()) { it.key.joinToString(nameInterpolator) }.toMap()
}

typealias Errors = Map<String, List<String>>
