package me.hyoseok.rest.template.example.service

import me.hyoseok.rest.template.example.entity.Product
import me.hyoseok.rest.template.example.repository.ProductRepository
import me.hyoseok.rest.template.example.service.dto.CreateProductResult
import me.hyoseok.rest.template.example.service.dto.UpdateProductStockResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.persistence.EntityManager

@Service
@Transactional
class ProductCommandService(
    private val productRepository: ProductRepository,
    private val productUpdateService: ProductUpdateService
) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun createProduct(name: String, price: Int, stockCount: Int): CreateProductResult {
        val product = Product(name = name, price = price, stockCount = stockCount)
        productRepository.save(product)
        return CreateProductResult(productId = product.id!!)
    }

    fun updateProduct(productId: Long, name: String, price: Int) {
        val productForUpdate: Product = findProduct(productId = productId)
        productForUpdate.changeProduct(name = name, price = price)
    }

    fun updateStockCount(productId: Long, stockCount: Int): UpdateProductStockResult {
        val productForUpdateStockCount: Product = findProduct(productId = productId)

        productForUpdateStockCount.decreaseStockCount(stockCount = stockCount)

        productRepository.flush()

        val updatedStockCount: Int =
            productUpdateService.findCurrentStockCount(productId = productId, stockCount = stockCount)

        productForUpdateStockCount.checkUpdatedStockCount(
            stockCount = stockCount,
            updatedStockCount = updatedStockCount
        )

        return UpdateProductStockResult(
            productId = productId,
            currentStockCount = productForUpdateStockCount.stockCount
        )
    }

    private fun findProduct(productId: Long): Product {
        return productRepository.findByIdOrNull(id = productId) ?: throw NoSuchElementException("상품이 존재하지 않습니다.")
    }
}
