package example.com.model

data class LoginRequest(
    val userEmail: String,
    val password: String
)

data class SignUpRequest(
    val userEmail: String,
    val password: String,
    val userName: String,
    val role: SystemRole,
)
