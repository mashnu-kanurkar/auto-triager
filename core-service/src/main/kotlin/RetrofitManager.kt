package example.com

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {
    private val baseURL = "http://localhost:8082"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    private val triagerService = retrofit.create(TriagerService::class.java)

    fun getTriagerServie(): TriagerService = triagerService?: throw Exception("The triager service is not responding")
    
    
}