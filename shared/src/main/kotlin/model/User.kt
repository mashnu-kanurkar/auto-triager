package example.com.model

import example.com.UpdateAllowedBy
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class User(

    @Contextual @BsonId val _id: String,

    val email: String,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    val name: String,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin)
    val teamIds: List<String>? = null,

    val organisationId: String? = null,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin)
    val role: SystemRole,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin)
    val designation: String = "user",

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    val status: Status = getDefaultStatuses().get(0),

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    val customProps: Map<String, String>? = null
)

