package me.hyoseok.rest.template.example.service.dto

data class UpdateProductStockResult(
    val productId: Long,
    val currentStockCount: Int
)
