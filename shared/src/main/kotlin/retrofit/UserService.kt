package example.com.retrofit

import example.com.model.SignUpRequest
import example.com.model.UpdateUserOption
import example.com.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {

    @GET("/user/auth/{email}")
    suspend fun getUserByEmail(email: String): Result<User>

    @POST("/user/auth/register")
    suspend fun registerUser(@Body signUpRequest: SignUpRequest): Result<User>

    @GET("/{orgId}/user")
    suspend fun getUserById(): Result<User>

    @POST("/{orgId}/update-self")
    suspend fun changeStatusByUser(@Body updateUserOption: UpdateUserOption): Result<String>

    @GET("/{orgId}/users")
    suspend fun getUserListById(): Result<List<User>>

    @POST("/{orgId}/update")
    suspend fun updateOneUser(): Result<String>

    @POST("/{orgId}/batch-update")
    suspend fun updateBatchUser(): Result<String>





}