package me.hyoseok.rest.template.example.repository

import me.hyoseok.rest.template.example.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import javax.persistence.LockModeType

interface ProductRepository : JpaRepository<Product, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT p from Product p WHERE p.id = :id")
    fun findProductByIdForUpdate(id: Long): Product?
}
