package me.hyoseok.rest.template.example.service

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class ApiService<T>(private val restTemplate: RestTemplate) {

    fun get(url: String, httpHeaders: HttpHeaders, clazz: Class<T>): ResponseEntity<T> {
        return callApiEndpoint(url, HttpMethod.GET, httpHeaders, null, clazz)
    }

    fun post(url: String, httpHeaders: HttpHeaders, body: Any?, clazz: Class<T>): ResponseEntity<T> {
        return callApiEndpoint(url, HttpMethod.GET, httpHeaders, body, clazz)
    }

    private fun callApiEndpoint(
        url: String, httpMethod: HttpMethod, httpHeaders: HttpHeaders, body: Any?, clazz: Class<T>
    ): ResponseEntity<T> {
        return restTemplate.exchange(url, httpMethod, HttpEntity(body, httpHeaders), clazz)
    }

}
