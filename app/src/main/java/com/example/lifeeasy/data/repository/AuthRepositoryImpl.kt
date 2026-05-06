package com.example.lifeeasy.data.repository

import com.example.lifeeasy.data.local.UserDao
import com.example.lifeeasy.data.local.UserEntity
import com.example.lifeeasy.domain.model.AuthResult
import com.example.lifeeasy.domain.model.User
import com.example.lifeeasy.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) : AuthRepository {

    // ── Session observation ────────────────────────────

    override val currentUser: Flow<User?>
        get() = userDao.getUser().map { entity ->
            entity?.let { User(it.id, it.name, it.email, it.lastSync) }
        }

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    // ── Email / Password ──────────────────────────────

    override suspend fun loginWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return AuthResult.Error("Login failed")
            val user = fetchOrCreateFirestoreUser(firebaseUser.uid, firebaseUser.displayName ?: "", email)
            saveUserLocally(user)
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Login failed")
        }
    }

    override suspend fun registerWithEmail(email: String, password: String, name: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return AuthResult.Error("Registration failed")

            // Set display name on Firebase profile
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            val user = User(
                id = firebaseUser.uid,
                name = name,
                email = email,
                lastSync = System.currentTimeMillis()
            )

            // Save to Firestore
            firestore.collection("users").document(firebaseUser.uid)
                .set(user).await()

            // Save locally
            saveUserLocally(user)
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Registration failed")
        }
    }

    // ── Google Sign-In ────────────────────────────────

    override suspend fun loginWithGoogleIdToken(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: return AuthResult.Error("Google sign-in failed")

            val user = fetchOrCreateFirestoreUser(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: ""
            )
            saveUserLocally(user)
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Google sign-in failed")
        }
    }

    // ── Logout ────────────────────────────────────────

    override suspend fun updateUserName(name: String): AuthResult {
        return try {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                // Update Firebase Profile
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()
                
                // Update Firestore
                val email = firebaseUser.email ?: ""
                val updatedUser = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = email,
                    lastSync = System.currentTimeMillis()
                )
                firestore.collection("users").document(firebaseUser.uid)
                    .set(updatedUser).await()
                
                // Update Local Database
                saveUserLocally(updatedUser)
                
                AuthResult.Success(updatedUser)
            } else {
                AuthResult.Error("No authenticated user found")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Failed to update profile")
        }
    }

    override suspend fun logout() {
        auth.signOut()
        userDao.deleteAll()
    }

    // ── Helpers ───────────────────────────────────────

    /**
     * Try to fetch the user document from Firestore.
     * If it doesn't exist yet (first Google login), create it.
     */
    private suspend fun fetchOrCreateFirestoreUser(uid: String, name: String, email: String): User {
        val docRef = firestore.collection("users").document(uid)
        val snapshot = docRef.get().await()

        return if (snapshot.exists()) {
            snapshot.toObject(User::class.java)
                ?: User(uid, name, email, System.currentTimeMillis())
        } else {
            val newUser = User(uid, name, email, System.currentTimeMillis())
            docRef.set(newUser).await()
            newUser
        }
    }

    private suspend fun saveUserLocally(user: User) {
        userDao.insertUser(
            UserEntity(
                id = user.id,
                name = user.name,
                email = user.email,
                lastSync = System.currentTimeMillis()
            )
        )
    }
}
