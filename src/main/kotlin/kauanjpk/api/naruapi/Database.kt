package kauanjpk.api.naruapi

import io.github.cdimascio.dotenv.dotenv
import java.sql.Connection
import java.sql.DriverManager

object Database {
    private val dotenv = dotenv()

    private val url = dotenv["DB_URL"]
    private val user = dotenv["DB_USER"]
    private val pass = dotenv["DB_PASS"]

    fun connect(): Connection {
        return DriverManager.getConnection(url, user, pass)
    }
}