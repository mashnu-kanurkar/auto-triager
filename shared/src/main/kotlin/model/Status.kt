package example.com.model

data class Status(
    val name: String,
    val upperLimit: Long? = null,
    val setLimit: Long? = null,
    val changedAt: Long? = null
)

val defaultStatus = listOf(Status(name = "Available"),
    Status("Busy", upperLimit = 30*60*1000, setLimit = 30*60*1000) )