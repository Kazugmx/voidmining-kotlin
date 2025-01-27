package org.example

import org.example.type.Blockchain
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.measureTimeMillis

private val json: Json
    get() = Json { encodeDefaults = true }

fun mineBlock(
    baseNonce: Int,
    prevHash: String,
    transactions: MutableList<String>,
    found: AtomicBoolean,
    it: Int
): Blockchain? {
    val endNonce = baseNonce + 1000

    for (nonce in baseNonce.rangeUntil(endNonce)) {
        if(nonce%1000000 ==0) println("Current nonce: $nonce")
        if (found.get()) return null

        val block = Blockchain(nonce, prevHash, transactions)
        val hash = block.hash()
        if (hash.startsWith("0000000")) {
            found.set(true)
            println("Block mined in thread num $it! Nonce: $nonce, hash: $hash")
            block.hash = hash
            return block
        }
    }
    return null
}

fun run() = runBlocking {

    val blocks: MutableList<Blockchain> = mutableListOf(
        Blockchain(
            0,
            "0",
            mutableListOf("aaa", "bbb", "ccc")
        )
    )
    val chainLength = 20


    val totalTime = measureTimeMillis {
        for (i in 1..chainLength) {
            println("current chain ${json.encodeToString(blocks)}")
            println("Mining block: $i...")
            var iteration = 0
            val prevBlock = blocks.last()
            val found = AtomicBoolean(false)
            val physCore = Runtime.getRuntime().availableProcessors()*2
            val iterRange = physCore * 1000

            val iterTime = measureTimeMillis {
                while (true) {
                    val baseNonce = iteration * iterRange

                    val jobs = List(physCore) { id ->
                        async(Dispatchers.IO) {
                            mineBlock(
                                baseNonce + id * 1000,
                                prevHash = prevBlock.hash,
                                found = found,
                                transactions = prevBlock.transaction,
                                it = iteration + id
                            )
                        }
                    }

                    val newBlock = jobs.awaitAll().filterNotNull().firstOrNull()
                    if (newBlock != null) {
                        blocks.add(newBlock)
                        break
                    }
                    iteration++
                }
            }
            println("Total time: %.3f seconds".format(iterTime.toDouble() / 1000))
        }
    }

    println("Blockchain completed! ${json.encodeToString(blocks)}")
    println("Total time: %.3f seconds".format(totalTime.toDouble() / 1000))
}

fun main() {
    run()
}