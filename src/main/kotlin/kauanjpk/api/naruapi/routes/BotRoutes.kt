package kauanjpk.api.naruapi.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import java.sql.DriverManager
import java.sql.Timestamp
import java.time.Instant
import java.time.Duration
import io.github.cdimascio.dotenv.dotenv

fun Route.botRoutes(jwtSecret: String) {
    val algorithm = Algorithm.HMAC256(jwtSecret)
    val dotenv = try { dotenv() } catch (_: Exception) { null }

    fun getEnv(key: String): String? =
        System.getenv(key) ?: dotenv?.get(key)

    fun getConnection() = DriverManager.getConnection(
        getEnv("DB_URL"),
        getEnv("DB_USER"),
        getEnv("DB_PASS")
    )

    fun updateBotStatus(botName: String, status: String) {
        getConnection().use { conn ->
            conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS bot_status (
                    bot_name VARCHAR(100) PRIMARY KEY,
                    status VARCHAR(50),
                    last_update TIMESTAMP
                )
            """).executeUpdate()
            conn.prepareStatement("""
                INSERT INTO bot_status (bot_name, status, last_update)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    status = VALUES(status),
                    last_update = VALUES(last_update)
            """).use { stmt ->
                stmt.setString(1, botName)
                stmt.setString(2, status)
                stmt.setTimestamp(3, Timestamp.from(Instant.now()))
                stmt.executeUpdate()
            }
        }
    }

    fun getBotStatus(botName: String): Map<String, Any> {
        getConnection().use { conn ->
            conn.prepareStatement("SELECT status, last_update FROM bot_status WHERE bot_name = ?").use { stmt ->
                stmt.setString(1, botName)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        val status = rs.getString("status") ?: "offline"
                        val ts = rs.getTimestamp("last_update")
                        val lastUpdate = ts?.toInstant() ?: Instant.EPOCH
                        val secondsAgo = Duration.between(lastUpdate, Instant.now()).seconds
                        val isActive = secondsAgo <= 90 && status == "online"

                        return mapOf(
                            "bot" to botName,
                            "status" to if (isActive) "online" else "offline",
                            "last_update" to lastUpdate.toString(),
                            "seconds_since_update" to secondsAgo
                        )
                    }
                }
            }
        }
        return mapOf(
            "bot" to botName,
            "status" to "unknown",
            "last_update" to Instant.EPOCH.toString(),
            "seconds_since_update" to -1
        )
    }

    post("/bot/update") {
        val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
        if (token == null) {
            call.respond(HttpStatusCode.Unauthorized, "Token ausente")
            return@post
        }

        try {
            val decoded = JWT.require(algorithm)
                .withIssuer("KotlinaruBot")
                .build()
                .verify(token)

            val botName = decoded.getClaim("bot_name").asString() ?: "Kotlinaru"
            val status = decoded.getClaim("status").asString() ?: "offline"

            updateBotStatus(botName, status)
            call.respond(HttpStatusCode.OK, mapOf(
                "message" to "Status atualizado com sucesso",
                "bot" to botName,
                "status" to status
            ))
        } catch (e: JWTVerificationException) {
            call.respond(HttpStatusCode.Forbidden, "Token inválido ou expirado")
        }
    }

    get("/bot/status") {
        val botName = call.request.queryParameters["bot_name"] ?: "Kotlinaru"
        val status = getBotStatus(botName)
        call.respond(HttpStatusCode.OK, status)
    }
}
