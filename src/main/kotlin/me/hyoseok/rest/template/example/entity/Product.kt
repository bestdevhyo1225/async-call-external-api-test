package me.hyoseok.rest.template.example.entity

import au.com.console.kassava.kotlinToString
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Product(name: String, price: Int, stockCount: Int) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    var name: String = name
        protected set

    var price: Int = price
        protected set

    var stockCount: Int = stockCount
        protected set

    override fun toString() = kotlinToString(properties = toStringProperties)

    companion object {
        private val toStringProperties = arrayOf(Product::id, Product::name, Product::price, Product::stockCount)
    }

    fun changeProduct(name: String, price: Int) {
        this.name = name
        this.price = price
    }

    fun decreaseStockCount(stockCount: Int) {
        if (this.stockCount - stockCount < 0) throw IllegalArgumentException("재고를 감소시킬 수 없습니다.")
        this.stockCount -= stockCount
    }

    fun checkZeroStockCount(stockCount: Int) {
        if (this.stockCount - stockCount < 0) throw IllegalStateException("재고 수량이 현재 0입니다.")
    }

    fun checkUpdatedStockCount(stockCount: Int, updatedStockCount: Int) {
        if (this.stockCount + stockCount != updatedStockCount) throw IllegalStateException("재고 수량이 맞지 않음")
    }
}
