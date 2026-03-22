package app.what.backend.auth

import java.security.SecureRandom
import java.util.Base64
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

interface AuthSessionStore {
    fun create(): AuthSession
    fun get(sessionId: String): AuthSession?
    fun delete(sessionId: String)
}

interface PartnerUserStore {
    fun upsert(profile: UserInfoResponse): Pair<PartnerUserRecord, Boolean>
    fun getAll(): List<PartnerUserRecord>
}

class InMemoryAuthSessionStore(
    sessionTtlMinutes: Long = 10
) : AuthSessionStore {
    private val random = SecureRandom()
    private val sessions = ConcurrentHashMap<String, AuthSession>()
    private val ttlMillis = sessionTtlMinutes * 60 * 1000L

    override fun create(): AuthSession {
        cleanupExpired()
        val session = AuthSession(
            id = UUID.randomUUID().toString(),
            state = randomUrlSafeToken(),
            nonce = randomUrlSafeToken(),
            createdAtEpochMillis = System.currentTimeMillis()
        )
        sessions[session.id] = session
        return session
    }

    override fun get(sessionId: String): AuthSession? {
        cleanupExpired()
        return sessions[sessionId]
    }

    override fun delete(sessionId: String) {
        sessions.remove(sessionId)
    }

    private fun cleanupExpired() {
        val now = System.currentTimeMillis()
        sessions.entries.removeIf { now - it.value.createdAtEpochMillis > ttlMillis }
    }

    private fun randomUrlSafeToken(size: Int = 32): String {
        val bytes = ByteArray(size)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}

class InMemoryPartnerUserStore : PartnerUserStore {
    private val usersBySubject = ConcurrentHashMap<String, PartnerUserRecord>()

    override fun upsert(profile: UserInfoResponse): Pair<PartnerUserRecord, Boolean> {
        val existing = usersBySubject[profile.sub]
        if (existing != null) {
            val updated = existing.copy(
                displayName = profile.name ?: existing.displayName,
                email = profile.email ?: existing.email,
                phone = profile.phoneNumber ?: existing.phone
            )
            usersBySubject[profile.sub] = updated
            return updated to false
        }

        val created = PartnerUserRecord(
            partnerUserId = UUID.randomUUID().toString(),
            sberSubjectId = profile.sub,
            displayName = profile.name ?: listOfNotNull(profile.givenName, profile.familyName).joinToString(" ").ifBlank { null },
            email = profile.email,
            phone = profile.phoneNumber,
            createdAtEpochMillis = System.currentTimeMillis()
        )
        usersBySubject[profile.sub] = created
        return created to true
    }

    override fun getAll(): List<PartnerUserRecord> = usersBySubject.values.sortedBy { it.createdAtEpochMillis }
}
