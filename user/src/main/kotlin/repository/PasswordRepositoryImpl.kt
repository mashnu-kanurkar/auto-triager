package example.com.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import example.com.models.DatabaseUser
import example.com.models.Password
import example.com.mongodb.MongoDBUtils
import example.com.repository.AnalystRepositoryImpl.Companion.USER_COLLECTION
import kotlinx.coroutines.flow.firstOrNull
import org.bson.BsonValue
import org.bson.types.ObjectId

class PasswordRepositoryImpl: PasswordRepository {
    private val database: MongoDatabase = MongoDBUtils.getUserDatabase()

    companion object {
        const val PASSWORD_COLLECTION = "password"
    }
    override suspend fun insert(password: Password): BsonValue? {
        try {
            val result = MongoDBUtils.getCollection<Password>(database, PASSWORD_COLLECTION, Password::class.java).insertOne(
                password
            )
            return result.insertedId
        } catch (e: MongoException) {
            System.err.println("Unable to insert record due to an error: $e")
        }
        return null
    }

    override suspend fun update(password: Password): Long {
        try {
            val query = Filters.eq("databaseUserId", password.databaseUserId)
            val options = UpdateOptions().upsert(true)
            val updates = Updates.combine(
                Updates.set(Password::password.name, password.password),
                Updates.set(Password::lastUpdate.name, password.lastUpdate),
                )
            val result =
                MongoDBUtils.getCollection<Password>(database, PASSWORD_COLLECTION, Password::class.java)
                    .updateOne(query, updates, options)
            return result.modifiedCount
        }catch (e: Exception){
            System.err.println("Unable to update record due to an error: $e")
            return -1
        }
    }

    override suspend fun read(userId: ObjectId): Password? {
        try {
            return MongoDBUtils.getCollection<Password>(database, PASSWORD_COLLECTION, Password::class.java).withDocumentClass<Password>()
                .find(Filters.eq("_id", userId)).firstOrNull()
        }catch (e: Exception){
            System.err.println("Unable to delete record due to an error: $e")
            return null
        }
    }

    override suspend fun delete(userId: ObjectId): Long {
        try {
            val result = MongoDBUtils.getCollection<Password>(database, PASSWORD_COLLECTION, Password::class.java).deleteOne(Filters.eq("_id", userId))
            return result.deletedCount
        } catch (e: MongoException) {
            System.err.println("Unable to delete record due to an error: $e")
            return -1
        }
    }
}