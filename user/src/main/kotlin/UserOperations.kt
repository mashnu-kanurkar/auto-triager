package example.com

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import example.com.model.RequestBody
import example.com.model.Response
import example.com.model.RoleNames
import example.com.model.SignUpRequest
import example.com.model.UpdateDataScope
import example.com.model.UpdateDataType
import example.com.model.User
import example.com.models.DatabaseUser
import example.com.models.Password
import example.com.models.toUser
import example.com.repository.AnalystRepository
import example.com.repository.AnalystRepositoryImpl
import example.com.repository.PasswordRepositoryImpl
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext
import org.bson.types.ObjectId

object UserOperations {
    private val analystRepository: AnalystRepository = AnalystRepositoryImpl()

    suspend fun getUserByEmail(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        //No JWT = This methods will be used before logged in
        val email = call.request.queryParameters.get("email").toString()
        if (email.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(error = "Email id mandatory"))
        }
        val databaseUser = analystRepository.findByEmail(email)
        if (databaseUser == null){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(error = "User not found"))
        }else{
            call.respond(status = HttpStatusCode.OK, message = Response.Success(data = databaseUser.toUser()))
        }
    }

    suspend fun insertUser(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        //No JWT = This methods will be used while signup
        val signUpRequest = call.receive<SignUpRequest>()
        val databaseUser = DatabaseUser(email = signUpRequest.userEmail,
            name = signUpRequest.userName,
            role = signUpRequest.role,
            organizationId = ObjectId(signUpRequest.orgId)
        )
        val userId = analystRepository.insertOne(databaseUser, signUpRequest.password)
        if (userId == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed(error = "unable to create user"))
        }else{
            val password = Password(databaseUserId = userId.asObjectId()?.value!!,
                password = signUpRequest.password,
                lastUpdate = System.currentTimeMillis())
            val result = PasswordRepositoryImpl().insert(password)
            if (result == null){
                call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed(error = "unable to create user"))
            }else{
                call.respond(status = HttpStatusCode.OK, message = Response.Success(data = databaseUser.toUser()))
            }
        }
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun deleteUser(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        validateOrgId(call)
        validateTeamId(call)
        val requestBody = call.receive<RequestBody>()
        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        var userIds = requestBody.d.filter {
            it.scope == UpdateDataScope.USER && it.type == UpdateDataType.DELETE
        }.map {
            ObjectId(it._id)
        }
        analystRepository.deleteById(userIds)
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    suspend fun findUserById(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        validateOrgId(call)
        validateTeamId(call)
        val principal = pipeline.call.principal<JWTPrincipal>()
        val userId = principal?.payload?.subject.toString()
        if (userId.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("User id is mandatory"))
        }
        val user = analystRepository.findById(objectId = ObjectId(userId))
        call.respond(status = HttpStatusCode.OK, message = Response.Success(data = user))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun findUserByOrgId(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        validateOrgId(call)
        val principal = call.principal<JWTPrincipal>()
        val orgId = principal?.payload?.getClaim("orgId").toString()
        if (orgId.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Organisation is mandatory"))
        }

        val users = analystRepository.findByOrgId(orgId = ObjectId(orgId))
        call.respond(status = HttpStatusCode.OK, message = Response.Success(data = users))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    suspend fun updateUser(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        validateOrgId(call)
        val principal = call.principal<JWTPrincipal>()
        val userRole = principal?.payload?.getClaim("role").toString()
        val requestBody = call.receive<RequestBody>()
        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        requestBody.d.forEach{updateData->
            validateFieldUpdates(updateData.data, userRole)
        }
        val updateOperations = requestBody.d.filter {
            it.scope == UpdateDataScope.USER && it.type == UpdateDataType.UPDATE }
            .map{
            val singleUserUpdateList = it.data.filter {
                it.key != User::_id.name && it.key != User::email.name
            }.map { (field, value) ->
                Updates.set(field, value)
            }
            val combinedUpdate = Updates.combine(singleUserUpdateList)
            val options = UpdateOptions().upsert(true)
            UpdateOneModel<DatabaseUser>(
                Filters.eq("_id", it._id), // Assuming "_id" is the field holding user IDs
                combinedUpdate,
                options
            )
        }
        analystRepository.updateMany(updateOperations)

    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    suspend fun getUsersFromTeam(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        validateOrgId(call)
        validateTeamId(call)
        val principal = call.principal<JWTPrincipal>()
        val teamId = principal?.payload?.getClaim("teamId").toString()

        val analystRepository: AnalystRepository = AnalystRepositoryImpl()
        val users = analystRepository.findByTeamId(teamId = ObjectId(teamId)).map { it.toUser() }
        call.respond(status = HttpStatusCode.OK, message = Response.Success(data = users))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun addUsersToTeam(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        validateOrgId(call)
        validateTeamId(call)
        val principal = call.principal<JWTPrincipal>()
        val teamId = principal?.payload?.getClaim("teamId").toString()
        val userList = call.receive<List<String>>() //list of userId
        val analystRepository: AnalystRepository = AnalystRepositoryImpl()
        val res = analystRepository.addUserToTeam(userList.map { ObjectId(it) }, ObjectId(teamId))
        if (res > 0){
            call.respond(status = HttpStatusCode.OK, message = Response.Success(data = "Updated $res records"))
        }else{
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to update user"))
        }
    }

    fun validateOrgId(call: ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val userOrgId = principal?.payload?.getClaim("orgId")
        val requestOrgId = call.parameters.get("orgId").toString()
        if (requestOrgId.isEmpty() || userOrgId.toString() != requestOrgId){
            throw IllegalAccessException("orgId mismatch: not authorised")
        }
    }
    fun validateTeamId(call: ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val userTeamIds = principal?.payload?.getClaim("teamIds")?.asList(String::class.java)
        val requestTeamId = call.parameters.get("teamId").toString()
        val doesNotHaveTeam = (userTeamIds?.contains(requestTeamId) == true).not()
        if (userTeamIds == null || userTeamIds.isEmpty() == true ||
            requestTeamId.isEmpty() || doesNotHaveTeam
        ){
            throw IllegalAccessException("teamId mismatch: not authorised")
        }
    }

}