package com.example.lifeeasy.data.local

/**
 * Tracks the synchronization status of each local record against Firebase Firestore.
 *
 * PENDING  – saved locally, not yet pushed to cloud
 * SYNCED   – successfully written to Firestore
 * FAILED   – sync attempt failed; will be retried by WorkManager
 */
enum class SyncStatus {
    PENDING,
    SYNCED,
    FAILED
}
