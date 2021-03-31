package me.hyoseok.rest.template.example.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.concurrent.CompletableFuture

@Service
@Transactional
class TransactionHandler(
    private val transactionAService: TransactionAService,
    private val transactionBService: TransactionBService
) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun handle() {

        logger.info("[ TransactionHandler ] Transaction  = " + TransactionSynchronizationManager.getCurrentTransactionName())

        val startTime: Long = System.currentTimeMillis()

        val aServiceResult: CompletableFuture<String> = transactionAService.execute()
        val bServiceResult: CompletableFuture<String> = transactionBService.execute()

        aServiceResult.thenCombine(bServiceResult) { result1, result2 -> "$result1 + $result2" }
            .thenAccept { combineResult ->
                val endTime: Long = System.currentTimeMillis()

                logger.info("[ TransactionHandler ] combineResult = $combineResult")
                logger.info("[ TransactionHandler ] execution time = " + (endTime - startTime))
            }
    }

    fun handleSync() {
        logger.info("[ TransactionHandler ] Transaction  = " + TransactionSynchronizationManager.getCurrentTransactionName())

        val startTime: Long = System.currentTimeMillis()

        val aServiceResult: String = transactionAService.executeSync()
        val bServiceResult: String = transactionBService.executeSync()

        val endTime: Long = System.currentTimeMillis()

        logger.info("[ TransactionHandler ] combineResult = $aServiceResult + $bServiceResult")
        logger.info("[ TransactionHandler ] execution time = " + (endTime - startTime))
    }
}
