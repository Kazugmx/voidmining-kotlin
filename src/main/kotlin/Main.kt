package org.example

import org.example.type.Blockchain
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicBoolean

fun mineBlock(baseNonce: Int, prevHash: String, transactions: MutableList<String>, found: AtomicBoolean,it: Int): Blockchain? {
    val startNonce = baseNonce
    val endNonce = baseNonce + 1000

    for (nonce in startNonce..endNonce) {
        if (found.get()) return null

        val block = Blockchain(nonce, prevHash, transactions)
        val hash = block.hash()
        if (hash.startsWith("00000")) {
            found.set(true)
            println("Block mined in thread num $it! Nonce: $nonce, hash: $hash")
            block.prevHash = hash
            return block
        }
    }
    return null
}

fun main() = runBlocking {
    val blocks: MutableList<Blockchain> = mutableListOf(Blockchain(1012939, "00000e687adb43ff4692bbc32a5fbc42108c992b3fbe16d2efd8232f0ac24e6e", mutableListOf("aaa", "bbb", "ccc")))
    for (i in 1..10) {
        println("current chain ${Json.encodeToString(blocks)}")
        println("Mining block: $i...")
        var iteration = 0
        val leastblock = blocks.last()
        val found = AtomicBoolean(false)
        while (true){
            val baseNonce = iteration * 10000

            val jobs = List(10) { id ->
                async(Dispatchers.Default) {
                    mineBlock(
                        baseNonce + id * 1000, prevHash = leastblock.prevHash, found = found, transactions = leastblock.transaction,it = iteration
                    )
                }
            }
            val newBlock = jobs.awaitAll().filterNotNull().firstOrNull()
            if(newBlock != null) {
                blocks.add(newBlock)
                leastblock.prevHash = newBlock.hash()
                break
            }
            iteration++
        }
    }
    println("Blockchain completed! ${Json.encodeToString(blocks)}")
    println("Transaction : ${blocks.last().transaction}")
}