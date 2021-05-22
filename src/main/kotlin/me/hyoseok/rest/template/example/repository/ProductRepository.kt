package me.hyoseok.rest.template.example.repository

import me.hyoseok.rest.template.example.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>
