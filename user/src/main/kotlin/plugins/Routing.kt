package example.com.plugins

import example.com.UserOperations
import example.com.authentication.JwtService
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun Application.configureRouting(){
    install(RoleBasedAuthorization)
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
    routing {
        route("/user"){
            route("/auth"){
                get("/{email}"){
                    tryOrReject{
                        UserOperations.getUserByEmail(it)
                    }
                }
                post("/register") {
                    tryOrReject {
                        UserOperations.insertUser(it)
                    }
                }
            }
            authenticate{
                route("/{orgId}") {
                    get("/user"){
                        tryOrReject{UserOperations.findUserById(it)}
                    }
                    get("/users"){
                        tryOrReject {
                            UserOperations.findUserByOrgId(it)
                        }
                    }
                    //update name, role, status, teamId
                    post("/update") {
                        tryOrReject {
                            UserOperations.updateUser(it)
                        }
                    }
                    post("/delete") {
                        tryOrReject {
                            UserOperations.deleteUser(it)
                        }
                    }
                    route("/{teamId}"){
                        get("/user"){
                            tryOrReject{UserOperations.findUserById(it)}
                        }
                        get("/users") {
                            tryOrReject {
                                UserOperations.getUsersFromTeam(it)
                            }
                        }
                        post("/add"){
                            tryOrReject {
                                UserOperations.addUsersToTeam(it)
                            }
                        }
                        //update user status only
                        post("/update") {
                            tryOrReject {
                                UserOperations.updateUser(it)
                            }
                        }
                        get("/delete") {
                            tryOrReject {
                                UserOperations.deleteUser(it)
                            }
                        }
                    }
                }
            }
        }
    }
}