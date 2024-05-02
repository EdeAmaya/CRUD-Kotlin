package Modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection? {

        try {
            val url = "jdbc:oracle:thin:@10.10.3.32:1521:xe"
            val user = "system"
            val contrasena = "desarrollo"

            val connection = DriverManager.getConnection(url,user,contrasena)
            return  connection
        }

        catch (e: Exception){
            println("Este es el error: $e")
            return null
        }
    }
}