package example.com.plugins

import example.com.model.Ticket
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting(){

    routing {
        post("/ticket"){
            val ticket = call.receive<Ticket>()
            
            //call.respond(user)
        }
    }
}