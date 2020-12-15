package me.hyoseok.rest.template.example.controller

import me.hyoseok.rest.template.example.service.ProductService
import org.slf4j.Logger
import org.slf4j.LoggerFactory.*
import org.springframework.http.ResponseEntity
import org.springframework.util.StopWatch
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping(value = ["api"])
class ApiController(
    private val productService: ProductService
) {

    private val logger: Logger = getLogger(ApiController::class.java)

    @GetMapping(value = ["/products/{id}"])
    fun findProduct(@PathVariable(value = "id") productId: Long): CompletableFuture<Any> {
        logger.info("[ Request ] productId : $productId")
        return productService.callGetExternalApi(productId)
    }

}
