package me.hyoseok.rest.template.example.service

import me.hyoseok.rest.template.example.controller.ApiController
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class AsyncService {

    private val logger: Logger = LoggerFactory.getLogger(ApiController::class.java)

    @Async(value = "threadPoolTaskExecutor")
    fun callAsyncFirst(): CompletableFuture<String> {
        try {
            val startTime: Long = System.currentTimeMillis()

            Thread.sleep(2000)

            val endTime: Long = System.currentTimeMillis()

            logger.info("[ Async ] execution time = " + (endTime - startTime))
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return CompletableFuture.completedFuture("callAsyncFirst")
    }

    @Async(value = "threadPoolTaskExecutor")
    fun callAsyncSecond(): CompletableFuture<String> {
        try {
            val startTime: Long = System.currentTimeMillis()

            Thread.sleep(3000)

            val endTime: Long = System.currentTimeMillis()

            logger.info("[ Async ] execution time = " + (endTime - startTime))
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return CompletableFuture.completedFuture("callAsyncSecond")
    }

    @Async(value = "threadPoolTaskExecutor")
    fun callAsyncMyName(): CompletableFuture<String> {
        try {
            Thread.sleep(2000)
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return CompletableFuture.completedFuture("callAsyncMyName")
    }
}
