package example.com.retrofit

import com.google.gson.GsonBuilder
import example.com.Constants
import example.com.utils.ServiceKey
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap

object RetrofitClientProvider {
    private val retrofitClientStore: ConcurrentHashMap<String, Retrofit> = ConcurrentHashMap()

    fun getRetrofitClient(serviceKey: ServiceKey): Retrofit? {
        return retrofitClientStore.getOrPut(serviceKey.key){
            createClient(serviceKey)
        }
    }

    private fun createClient(serviceKey: ServiceKey): Retrofit{
        val port =  "${Constants.domain}:${serviceKey.port}"
        return Retrofit.Builder()
            .baseUrl(port)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }
    inline fun <reified T> getService(retrofit: Retrofit?): T{
        return retrofit.create(T::class.java)
    }
}
