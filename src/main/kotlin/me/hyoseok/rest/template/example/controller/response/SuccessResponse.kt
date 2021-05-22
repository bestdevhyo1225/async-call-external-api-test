package me.hyoseok.rest.template.example.controller.response

data class SuccessResponse(
    val status: String = "success",
    val data: Any
)
