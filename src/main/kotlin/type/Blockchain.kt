package org.example.type

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.MessageDigest

@Serializable
data class Blockchain(
    var nonce: Int = 0,
    var prevHash: String = "",
    val transaction: MutableList<String> = mutableListOf("aaa", "bbb", "ccc").toMutableList()
){
    private fun toJson() = Json.encodeToString(this)

    fun hash():String{
        val input = toJson()
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}