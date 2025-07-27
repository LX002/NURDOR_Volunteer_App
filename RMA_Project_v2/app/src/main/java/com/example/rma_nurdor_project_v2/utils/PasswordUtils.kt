package com.example.rma_nurdor_project_v2.utils
import org.mindrot.jbcrypt.BCrypt
object PasswordUtils {

    fun hashPassword(plainPassword: String): String {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt())
    }

    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }
}