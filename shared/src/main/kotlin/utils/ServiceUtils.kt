package example.com.utils


enum class ServiceKey(val key: String, val port: String){
    SHARED("shared", "8081"),
    CORE("core", "8082"),
    ORGANISATION("organisation","8083"),
    USER("analyst","8084"),
    TRIAGER("triager","8085"),
    NOTIFICATION("notification","8086"),
    AUTHENTICATION("authentication","8087"),
    API_GATEWAY("api-gateway","8088")

}