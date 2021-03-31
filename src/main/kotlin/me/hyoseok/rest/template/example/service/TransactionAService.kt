package me.hyoseok.rest.template.example.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.concurrent.CompletableFuture

@Service
@Transactional
class TransactionAService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Async(value = "threadPoolTaskExecutor")
    fun execute(): CompletableFuture<String> {

        logger.info("[ A Service ] Transaction  = " + TransactionSynchronizationManager.getCurrentTransactionName())

        try {
            Thread.sleep(3000)
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return CompletableFuture.completedFuture("Success A Service!!")
    }

    fun executeSync(): String {

        logger.info("[ A Service ] Transaction  = " + TransactionSynchronizationManager.getCurrentTransactionName())

        try {
            Thread.sleep(3000)
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return "Success A Service!!"
    }
}
