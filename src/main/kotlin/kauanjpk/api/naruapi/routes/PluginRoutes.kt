package kauanjpk.api.naruapi.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant
import java.sql.Timestamp
import java.util.concurrent.ConcurrentLinkedQueue

fun getEnv(key: String): String? =
    System.getenv(key) ?: dotenv()?.get(key)

fun Route.pluginRoutes() {

    val dotenv = try { dotenv() } catch (_: Exception) { null }
    val jwtSecret = getEnv("JWT_SECRET") ?: error("JWT_SECRET não definido")
    val algorithm = Algorithm.HMAC256(jwtSecret)


    val serverToBot = ConcurrentLinkedQueue<String>()
    val botToServer = ConcurrentLinkedQueue<String>()


    post("/chatLogs/fromServer") {
        val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim() ?: return@post call.respond(
            HttpStatusCode.Unauthorized,
            "Token ausente"
        )

        try {
            JWT.require(algorithm).withIssuer("KotlinaruBot").build().verify(token)
            val data = call.receive<Map<String, String>>()
            val message = data["message"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mensagem vazia")
            serverToBot.add(message)
            call.respond(HttpStatusCode.OK, "Mensagem recebida")
        } catch (e: JWTVerificationException) {
            call.respond(HttpStatusCode.Forbidden, "Token inválido ou expirado")
        }
    }


    get("/chatLogs/fromServer") {
        val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim() ?: return@get call.respond(
            HttpStatusCode.Unauthorized,
            "Token ausente"
        )

        try {
            JWT.require(algorithm).withIssuer("NaruBot").build().verify(token)
            val messages = mutableListOf<String>()
            while (serverToBot.isNotEmpty()) messages.add(serverToBot.poll())
            call.respond(HttpStatusCode.OK, messages)
        } catch (e: JWTVerificationException) {
            call.respond(HttpStatusCode.Forbidden, "Token inválido ou expirado")
        }
    }

    /** ✅ Recebe mensagens do bot Discord **/
    post("/chatLogs/fromBot") {
        val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim() ?: return@post call.respond(
            HttpStatusCode.Unauthorized,
            "Token ausente"
        )

        try {
            JWT.require(algorithm).withIssuer("NaruBot").build().verify(token)
            val data = call.receive<Map<String, String>>()
            val sender = data["sender"] ?: "Desconhecido"
            val message = data["message"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mensagem vazia")
            botToServer.add("§b[$sender] §f$message")
            call.respond(HttpStatusCode.OK, "Mensagem recebida")
        } catch (e: JWTVerificationException) {
            call.respond(HttpStatusCode.Forbidden, "Token inválido ou expirado")
        }
    }

    /** ✅ Envia mensagens do bot para o servidor **/
    get("/chatLogs/fromBot") {
        val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim() ?: return@get call.respond(
            HttpStatusCode.Unauthorized,
            "Token ausente"
        )

        try {
            JWT.require(algorithm).withIssuer("KotlinaruBot").build().verify(token)
            val messages = mutableListOf<String>()
            while (botToServer.isNotEmpty()) messages.add(botToServer.poll())
            call.respond(HttpStatusCode.OK, messages)
        } catch (e: JWTVerificationException) {
            call.respond(HttpStatusCode.Forbidden, "Token inválido ou expirado")
        }
    }


    get("/chatLogs/status") {
        val status = mapOf(
            "status" to "online",
            "timestamp" to Timestamp.from(Instant.now()).toString()
        )
        call.respond(HttpStatusCode.OK, status)
    }
}
