package com.example.favouriteplaces

data class Place(
    val title: String? = null,
    var description: String? = null,
    val lat: Long? = null,
    val lng: Long? = null,
    val category: String? = null,
    val stars: Double? = null,
    val review: String? = null,
    val public: Boolean? = null,
    val author: String? = null
) {
}