package me.hyoseok.rest.template.example.service

import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class ProductService(
    private val apiService: ApiService<Any>,
) {

    @Async(value = "threadPoolTaskExecutor")
    fun callGetExternalApi(id: Long): CompletableFuture<Any> {
        return CompletableFuture.completedFuture(
            apiService
                .get("http://localhost:8080/admin/api/v1/products/$id", HttpHeaders.EMPTY, Any::class.java)
                .body
        )
    }
}
