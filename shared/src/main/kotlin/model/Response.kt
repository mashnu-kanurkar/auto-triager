package example.com.model

sealed class Response<out T> {
    class Success<out T>(val data: T): Response<T>()
    class Failed(val error: String): Response<Nothing>()
}