package example.com.repository

import example.com.models.Password
import org.bson.BsonValue
import org.bson.types.ObjectId

interface PasswordRepository {
    suspend fun insert(password: Password): BsonValue?
    suspend fun update(password: Password): Long
    suspend fun read(userId: ObjectId): Password
    suspend fun delete(userId: ObjectId): Long
}