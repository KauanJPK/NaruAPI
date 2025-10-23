package kauanjpk.api.naruapi

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import kauanjpk.api.naruapi.routes.pluginRoutes
import kauanjpk.api.naruapi.routes.botRoutes
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.response.respondText
import java.sql.DriverManager

fun main() {
    val dotenv = try { dotenv() } catch (_: Exception) { null }

    fun getEnv(key: String): String? =
        System.getenv(key) ?: dotenv?.get(key)

    val jwtSecret = getEnv("JWT_SECRET") ?: error("JWT_SECRET não definido")
    val port = getEnv("PORT")?.toIntOrNull() ?: 8080
    val dbUrl = getEnv("DB_URL") ?: error("DB_URL não definido")
    val dbUser = getEnv("DB_USER") ?: error("DB_USER não definido")
    val dbPass = getEnv("DB_PASS") ?: error("DB_PASS não definido")

    // Cria a tabela de status do bot, caso não exista
    DriverManager.getConnection(dbUrl, dbUser, dbPass).use { conn ->
        conn.createStatement().use { stmt ->
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS bot_status (
                    bot_name VARCHAR(100) PRIMARY KEY,
                    status VARCHAR(50),
                    last_update TIMESTAMP
                )
            """.trimIndent())
            println("✅ Tabela bot_status verificada/criada")
        }
    }

    embeddedServer(Netty, port = port) {
        install(ContentNegotiation) { jackson() }

        routing {
            get("/") { call.respondText("🌐 NaruAPI está online") }


            pluginRoutes()

            botRoutes(jwtSecret)
        }

        println("🚀 NaruAPI rodando na porta $port")
    }.start(wait = true)
}
