package example.com

import example.com.model.User
import kotlin.reflect.KProperty1


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresRole(vararg val systemRole: String)

fun checkRole(roles: Array<out String>, userRole: String) {
    if (userRole !in roles) {
        throw IllegalAccessException("Access denied for role: $userRole")
    }
}

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class UpdateAllowedBy(vararg val systemRole: String)

fun validateFieldUpdates(user: User, updatedFields: Map<String, Any>, currentUserRole: String) {
    val userClass = user::class
    updatedFields.forEach { (fieldName, _) ->
        // Find the property in the User class by name
        val property = userClass.members.find { it.name == fieldName } as? KProperty1<*, *>

        // Get the annotation from the property
        val annotation = property?.annotations?.filterIsInstance<UpdateAllowedBy>()?.firstOrNull()

        // Check if the current role is allowed to update the field
        if (annotation != null && currentUserRole !in annotation.systemRole) {
            throw IllegalAccessException("Role $currentUserRole is not allowed to update $fieldName")
        }
    }
}



