package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.UserDao
import com.example.lifeeasy.data.local.UserEntity
import com.example.lifeeasy.domain.model.User
import com.example.lifeeasy.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {

    override fun getUser(): Flow<User?> {
        return userDao.getUser().map { entity ->
            entity?.let { User(it.id, it.name, it.email, it.lastSync) }
        }
    }

    override suspend fun saveUser(user: User) {
        userDao.insertUser(UserEntity(user.id, user.name, user.email, user.lastSync))
    }

    override suspend fun syncWithCloud() {
        val currentUser = auth.currentUser ?: return
        try {
            val snapshot = firestore.collection("users").document(currentUser.uid).get().await()
            val cloudUser = snapshot.toObject(User::class.java)
            cloudUser?.let { saveUser(it) }
        } catch (e: Exception) {
            // Handle error
        }
    }
}
