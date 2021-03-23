package me.hyoseok.rest.template.example.service

import me.hyoseok.rest.template.example.controller.ApiController
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SyncService {

    private val logger: Logger = LoggerFactory.getLogger(ApiController::class.java)

    fun callSyncFirst(): String {
        try {
            val startTime: Long = System.currentTimeMillis()

            Thread.sleep(2000)

            val endTime: Long = System.currentTimeMillis()

            logger.info("[ Sync ] execution time = " + (endTime - startTime))
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return "callSyncFirst"
    }

    fun callSyncSecond(): String {
        try {
            val startTime: Long = System.currentTimeMillis()

            Thread.sleep(3000)

            val endTime: Long = System.currentTimeMillis()

            logger.info("[ Sync ] execution time = " + (endTime - startTime))
        } catch (exception: InterruptedException) {
            exception.printStackTrace()
        }

        return "callSyncSecond"
    }
}
