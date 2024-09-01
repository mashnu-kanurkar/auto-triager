package example.com.authentication

import example.com.model.Response
import example.com.model.SignUpRequest
import example.com.model.User
import example.com.retrofit.RetrofitClientProvider
import example.com.retrofit.UserService
import example.com.utils.ServiceKey
import org.mindrot.jbcrypt.BCrypt


class AuthenticationProvider(){

    suspend fun login(email: String, password: String): Response<User>{
        val retrofit = RetrofitClientProvider.getRetrofitClient(ServiceKey.ANALYST)
        val userService = RetrofitClientProvider.getService<UserService>(retrofit)
        val user = userService?.getUserByEmail(email)
        return if (user?.let { verifyPassword(password, it.second) } ==true){
            Response.Success(user.first)
        }else{
            Response.Failed("Login failed")
        }
    }

    suspend fun registerUser(signUpRequest: SignUpRequest): Response<User> {
        val hashedPassword = hashPassword(signUpRequest.password)
        val retrofit = RetrofitClientProvider.getRetrofitClient(ServiceKey.ANALYST)
        val userService = RetrofitClientProvider.getService<UserService>(retrofit)
        userService?.let {
            val existingUser = userService.getUserByEmail(signUpRequest.userEmail)
            if (existingUser != null){
                return Response.Failed("User already exist")
            }
        }
        val signUpData = signUpRequest.copy(password = hashedPassword)
        val user = userService?.registerUser(signUpData)
        if (user != null){
            return Response.Success(user)
        }
        return Response.Failed("Sign up failed")
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }

    fun hashPassword(password: String): String {
        val salt = BCrypt.gensalt(12) // 12 is the default cost factor, which is secure and performant for most cases
        return BCrypt.hashpw(password, salt)
    }
}