package example.com.plugins

import example.com.UserOperations
import example.com.authentication.JwtService
import example.com.retrofit.*
import example.com.model.LoginRequest
import example.com.model.SignUpRequest
import example.com.utils.ServiceKey
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val retrofit = RetrofitClientProvider.getRetrofitClient(ServiceKey.API_GATEWAY)
    install(Authentication) {
        jwt {
            verifier(JwtService().verifier)
            validate { credential ->
                // Validate the claims in the token
                val userId = credential.payload.subject
                val email = credential.payload.getClaim("email").asString()
                val role = credential.payload.getClaim("role").asString()
                val orgId = credential.payload.getClaim("orgId").asString()
                val expirationTime = credential.payload.expiresAt.time

                // Perform necessary checks
                if (userId != null && email.isNotEmpty() && role.isNotEmpty() && orgId.isNotEmpty()) {
                    // Optionally: Check if the token is expired
                    if (System.currentTimeMillis() > expirationTime) {
                        null  // Token has expired
                    } else {
                        JWTPrincipal(credential.payload)
                    }
                } else {
                    null  // Invalid token
                }
            }
        }
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        route("/auth"){
            val authService = RetrofitClientProvider.getService<AuthenticationService>(retrofit)
            post("/login") {
                val loginRequest = call.receive<LoginRequest>()
                retrofit?.let {
                    val authService = RetrofitClientProvider.getService<AuthenticationService>(it)
                    authService?.login(loginRequest)?:
                    call.respondText(text = "500: something went wrong" , status = HttpStatusCode.InternalServerError)
                }

            }
            post("/signup"){
                val signUpRequest = call.receive<SignUpRequest>()
                authService?.signUp(signUpRequest)?:
                call.respondText(text = "500: something went wrong" , status = HttpStatusCode.InternalServerError)
            }
        }
        authenticate{
            route("/{orgId}") {
                get("/user"){
                    call.tryOrReject {
                        UserOperations.findUserById(it)
                    }
                }
                post("/update-self") {
                    call.tryOrReject {
                        UserOperations.changeStatusByUser(it)
                    }
                }
                get("/users"){
                    call.tryOrReject {
                        UserOperations.findUserByOrgId(it)
                    }
                }
                //update name, role, status, teamId
                post("/update") {
                    call.tryOrReject {
                        UserOperations.updateOneUser(it)
                    }
                }
                post("/batch-update") {
                    call.tryOrReject {
                        UserOperations.updateManyUsers(it)
                    }
                }
                post("/delete") {
                    call.tryOrReject {
                        UserOperations.removeUser(it)
                    }
                }
                route("/{teamId}"){
                    post("/configure"){

                    }
                    get("/users") {
                        call.tryOrReject {
                            UserOperations.getUsersFromTeam(it)
                        }
                    }
                    post("/add"){
                        call.tryOrReject {
                            UserOperations.addUsersToTeam(it)
                        }
                    }
                    //update user status only
                    post("/update") {
                        call.tryOrReject {
                            UserOperations.updateCustomPropOneUser(it)
                        }
                    }
                    post("/batch-update"){
                        call.tryOrReject {
                            UserOperations.updateCustomPropManyUsers(it)
                        }
                    }
                    get("/delete") {
                        call.tryOrReject {
                            UserOperations.removeUserFromTeam(it)
                        }
                    }
                }
            }
        }


    }
}
