package ru.kpnn

import kotlinx.serialization.Serializable

typealias ProductId = String

@Serializable
data class Product (val productId: ProductId, val name: String, val description: String, val icon: String?)

var maxId = 0;

fun generateProductId () : ProductId {
    val id = maxId;
    maxId++;
    return id.toString();
}

val productStorage = mutableMapOf<ProductId, Product>()