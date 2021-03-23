package me.hyoseok.rest.template.example.controller

import me.hyoseok.rest.template.example.service.AsyncService
import me.hyoseok.rest.template.example.service.ProductService
import me.hyoseok.rest.template.example.service.SyncService
import org.slf4j.Logger
import org.slf4j.LoggerFactory.*
import org.springframework.http.ResponseEntity
import org.springframework.util.StopWatch
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping(value = ["api"])
class ApiController(
    private val productService: ProductService,
    private val asyncService: AsyncService,
    private val syncService: SyncService
) {

    private val logger: Logger = getLogger(ApiController::class.java)

    @GetMapping(value = ["/products/{id}"])
    fun findProduct(@PathVariable(value = "id") productId: Long): CompletableFuture<Any> {
        logger.info("[ Request ] productId : $productId")
        return productService.callGetExternalApi(productId)
    }

    @GetMapping(value = ["/call-sync"])
    fun callSync() {
        val startTime: Long = System.currentTimeMillis()

        val firstResult: String = syncService.callSyncFirst()
        val secondResult: String = syncService.callSyncSecond()

        val endTime: Long = System.currentTimeMillis()

        val s = firstResult + secondResult

        logger.info("[ Sync Result ] value $s")
        logger.info("[ Sync Result ] execution time = " + (endTime - startTime))
    }

    @GetMapping(value = ["/call-async"])
    fun callAsync() {
        val startTime: Long = System.currentTimeMillis()

        val firstResult: CompletableFuture<String> = asyncService.callAsyncFirst()
        val secondResult: CompletableFuture<String> = asyncService.callAsyncSecond()

        firstResult.thenCombine(secondResult) { s1, s2 -> "$s1 + $s2" }
            .thenAccept { s ->
                val endTime: Long = System.currentTimeMillis()
                logger.info("[ Async Result ] value $s")
                logger.info("[ Async Result ] execution time = " + (endTime - startTime))
            }
    }
}
