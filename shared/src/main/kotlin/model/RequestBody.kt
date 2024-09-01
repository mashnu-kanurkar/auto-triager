package example.com.model

data class UpdateData(
    val _id: String, //userId or orgId or teamId which needs to be updated
    val type: UpdateDataType, //update or delete
    val scope: UpdateDataScope, //user or org or team
    val data: Map<String, String>
)
data class RequestBody(
    val d: List<UpdateData>
)
enum class UpdateDataType{
    UPDATE,
    DELETE
}

enum class UpdateDataScope{
    USER,
    ORG,
    TEAM
}


//data class UpdateUserOption(
//    val name: String? = null,
//    val teamId: String? = null,
//    val role: SystemRole? = null,
//    val status: Status? = null,
//)

/***
 * {
 * "d":[
 * {"_id":"user id",
 * "type":"update",
 * "scope":"user",
 * data:{
 * "role":"admin" // any user property except _id, email and organisationId
 * }
 * }
 * ]
 * }
 */
