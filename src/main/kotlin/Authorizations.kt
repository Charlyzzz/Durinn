import io.konform.validation.Validation

typealias AuthorizedUsers = Map<String, List<String>>

fun validate(authorizationAttempt: AuthorizationAttempt, authorizedUsers: AuthorizedUsers): AuthenticationResult {
    val authorizedUser = authorizedUsers.entries.find { it.value.contains(authorizationAttempt.uid) }
    return authorizedUser?.let { AuthenticationResult(isAuthorized = true, name = it.key) }
        ?: AuthenticationResult(isAuthorized = false)
}

data class AuthenticationResult(val isAuthorized: Boolean, val name: String? = null)

data class AuthorizationAttempt(val uid: String?) {
    companion object {
        val validate = Validation<AuthorizationAttempt> {
            AuthorizationAttempt::uid required {
                notBlank()
            }
        }
    }
}
