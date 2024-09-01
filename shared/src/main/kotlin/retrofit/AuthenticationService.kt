package example.com.Retrofit

import retrofit2.http.POST

interface AuthenticationService {
    @POST("login")
    suspend fun login()
}