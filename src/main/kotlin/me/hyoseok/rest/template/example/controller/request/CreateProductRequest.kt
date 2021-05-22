package me.hyoseok.rest.template.example.controller.request

data class CreateProductRequest(
    val name: String,
    val price: Int,
    val stockCount: Int,
)
