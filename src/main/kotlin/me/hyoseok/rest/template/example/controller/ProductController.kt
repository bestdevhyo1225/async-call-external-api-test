package me.hyoseok.rest.template.example.controller

import me.hyoseok.rest.template.example.controller.request.CreateProductRequest
import me.hyoseok.rest.template.example.controller.request.UpdateProductRequest
import me.hyoseok.rest.template.example.controller.request.UpdateProductStockRequest
import me.hyoseok.rest.template.example.controller.response.SuccessResponse
import me.hyoseok.rest.template.example.service.ProductCommandService
import me.hyoseok.rest.template.example.service.ProductCommandLockService
import me.hyoseok.rest.template.example.service.dto.CreateProductResult
import me.hyoseok.rest.template.example.service.dto.UpdateProductStockResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping(value = ["/products"])
class ProductController(
    private val productCommandService: ProductCommandService,
    private val productCommandLockService: ProductCommandLockService
) {

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    fun create(@RequestBody request: CreateProductRequest): ResponseEntity<SuccessResponse> {
        val createProductResult: CreateProductResult = productCommandService.createProduct(
            name = request.name,
            price = request.price,
            stockCount = request.stockCount
        )
        return ResponseEntity.created(URI.create("/products/" + createProductResult.productId))
            .body(SuccessResponse(data = createProductResult))
    }

    @PatchMapping(value = ["/{id}"])
    fun update(
        @PathVariable(value = "id") productId: Long,
        @RequestBody request: UpdateProductRequest
    ): ResponseEntity<SuccessResponse> {
        productCommandService.updateProduct(productId = productId, name = request.name, price = request.price)
        return ResponseEntity.ok(SuccessResponse(data = object {
            val isOk = true
        }))
    }

    @PatchMapping(value = ["/{id}/stock-count"])
    fun updateStockCount(
        @PathVariable(value = "id") productId: Long,
        @RequestBody request: UpdateProductStockRequest
    ): ResponseEntity<SuccessResponse> {
        val updateProductStockResult: UpdateProductStockResult =
            productCommandService.updateStockCount(productId = productId, stockCount = request.stockCount)

        return ResponseEntity.ok(SuccessResponse(data = updateProductStockResult))
    }

    @PatchMapping(value = ["/lock/{id}/stock-count"])
    fun updateStockCountWithLock(
        @PathVariable(value = "id") productId: Long,
        @RequestBody request: UpdateProductStockRequest
    ): ResponseEntity<SuccessResponse> {
        val updateProductStockResult: UpdateProductStockResult =
            productCommandLockService.updateStockCountWithLock(productId = productId, stockCount = request.stockCount)

        return ResponseEntity.ok(SuccessResponse(data = updateProductStockResult))
    }
}
