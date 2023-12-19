package com.example.favouriteplaces

data class Place(
    val name: String,
    val lat: Long,
    val lng: Long,
    val category: String,
    val stars: Double,
    val review: String,
    val public: Boolean,
    val author: String
) {
}