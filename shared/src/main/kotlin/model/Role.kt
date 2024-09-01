package example.com.model

enum class SystemRole(val roleName: String){
    SYSTEM (RoleNames.system),
    CREATOR(RoleNames.creator),
    ADMIN(RoleNames.admin),
    TEAM_ADMIN(RoleNames.teamAdmin),
    USER(RoleNames.user)
}

object RoleNames{
    const val system = "System"
    const val creator = "Creator"
    const val admin = "Admin"
    const val teamAdmin = "Team_Admin"
    const val user = "User"
}

