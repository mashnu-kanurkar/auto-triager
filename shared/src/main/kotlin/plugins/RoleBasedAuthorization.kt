package example.com.plugins


import example.com.RequiresRole
import example.com.checkRole
import example.com.model.Response
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.full.findAnnotation

// Create a key for storing and retrieving the function handler
val FunctionHandlerKey = AttributeKey<Any>("FunctionHandlerKey")

val RoleBasedAuthorization = createRouteScopedPlugin("RoleBasedAuthorization") {
    onCall { call ->
        val principal = call.principal<JWTPrincipal>()
        val userRole = principal?.payload?.getClaim("role")?: throw IllegalAccessException("Missing role in request")
        // Retrieve the function handler from attributes
        val functionHandler = call.attributes[FunctionHandlerKey]
        // Reflectively find the Role annotation on the function handler
        val roleAnnotation = functionHandler::class.findAnnotation<RequiresRole>()
        roleAnnotation?.let {
            checkRole(it.systemRole, userRole.toString())
        }
    }
}

suspend fun ApplicationCall.tryOrReject(block: suspend (call: ApplicationCall)-> Unit){
    try {
        block.invoke(this)
    }catch (e: IllegalAccessException){
        this.respond(status = HttpStatusCode.Unauthorized, message = Response.Failed(e.message.toString()))
    }
    catch (e: Exception){
        this.respond(status = HttpStatusCode.InternalServerError, message = Response.Success(data = e.message))
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.tryOrReject(block: suspend (call: ApplicationCall, coroutineContext: CoroutineContext)-> Unit){
    try {
        block.invoke(this.call, this.coroutineContext)
    }catch (e: IllegalAccessException){
        this.call.respond(status = HttpStatusCode.Unauthorized, message = Response.Failed(e.message.toString()))
    }
    catch (e: Exception){
        this.call.respond(status = HttpStatusCode.InternalServerError, message = Response.Success(data = e.message))
    }
}

