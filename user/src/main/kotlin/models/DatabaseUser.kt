package example.com.models

import example.com.model.Status
import example.com.model.SystemRole
import example.com.model.User
import example.com.model.getDefaultStatuses
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class DatabaseUser(
    @Contextual @BsonId val _id: ObjectId = ObjectId(),
    val email: String,
    val name: String,
    val teamIds: List<ObjectId>? = null,
    val organizationId: ObjectId,
    val role: SystemRole,
    val status: Status = getDefaultStatuses().get(0),
    val designation: String = "user",
    val customProps: Map<String, String>? = null
)

@Serializable
data class Password(
    @Contextual val databaseUserId: ObjectId,
    val password: String,
    val lastUpdate: Long,
)

fun DatabaseUser.toUser(): User{
    return User(_id=_id.toString(), email = email,
        name = name, teamIds = teamIds?.map { it.toString() },
        organisationId = organizationId.toString(), role = role,
        status = status,
        designation = designation,
        customProps = customProps)
}

fun User.toDatabaseUser(): DatabaseUser{
    return DatabaseUser(_id = ObjectId(_id), email = email,
        name = name, teamIds = teamIds?.map { ObjectId(it) },
        organizationId = ObjectId(organisationId),
        role = role, status = status, designation = designation, customProps = customProps)
}
