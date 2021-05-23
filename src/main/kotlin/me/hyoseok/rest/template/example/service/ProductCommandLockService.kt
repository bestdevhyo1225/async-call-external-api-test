package me.hyoseok.rest.template.example.service

import me.hyoseok.rest.template.example.entity.Product
import me.hyoseok.rest.template.example.repository.ProductRepository
import me.hyoseok.rest.template.example.service.dto.UpdateProductStockResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductCommandLockService(
    private val productRepository: ProductRepository
) {

    fun updateStockCountWithLock(productId: Long, stockCount: Int): UpdateProductStockResult {
        val product: Product =
            productRepository.findProductByIdForUpdate(id = productId) ?: throw NoSuchElementException("상품이 존재하지 않습니다.")

        product.decreaseStockCount(stockCount = stockCount)

        return UpdateProductStockResult(
            productId = productId,
            currentStockCount = product.stockCount
        )
    }
}
