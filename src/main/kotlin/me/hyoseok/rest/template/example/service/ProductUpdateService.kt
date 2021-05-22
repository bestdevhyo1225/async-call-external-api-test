package me.hyoseok.rest.template.example.service

import me.hyoseok.rest.template.example.entity.Product
import me.hyoseok.rest.template.example.repository.ProductRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.persistence.EntityManager

@Service
class ProductUpdateService(
    private val productRepository: ProductRepository
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun findCurrentStockCount(productId: Long, stockCount: Int): Int {
        val updatedProduct: Product =
            productRepository.findByIdOrNull(id = productId) ?: throw NoSuchElementException("상품이 존재하지 않습니다.")

        updatedProduct.checkZeroStockCount(stockCount = stockCount)

        return updatedProduct.stockCount
    }
}
