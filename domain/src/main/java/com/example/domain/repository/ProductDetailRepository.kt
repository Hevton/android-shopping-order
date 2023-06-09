package com.example.domain.repository

import com.example.domain.model.Product

interface ProductDetailRepository {
    fun getById(id: Long): Result<Product>
}
