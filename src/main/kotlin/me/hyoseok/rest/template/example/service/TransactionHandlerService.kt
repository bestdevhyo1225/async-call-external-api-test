package me.hyoseok.rest.template.example.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CompletableFuture

@Service
@Transactional
class TransactionHandlerService(
    private val transactionAService: TransactionAService,
    private val transactionBService: TransactionBService
) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun handle() {
        val startTime: Long = System.currentTimeMillis()

        val aServiceResult: CompletableFuture<String> = transactionAService.execute()
        val bServiceResult: CompletableFuture<String> = transactionBService.execute()

        aServiceResult.thenCombine(bServiceResult) { result1, result2 -> "$result1 + $result2" }
            .thenAccept { combineResult ->
                val endTime: Long = System.currentTimeMillis()

                logger.info("[ TransactionHandlerService ] combineResult = $combineResult")
                logger.info("[ TransactionHandlerService ] execution time = " + (endTime - startTime))
            }
    }
}
