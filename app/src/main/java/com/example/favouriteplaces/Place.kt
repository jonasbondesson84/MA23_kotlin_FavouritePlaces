package com.example.favouriteplaces

import com.google.firebase.firestore.DocumentId

data class Place( @DocumentId var docID: String? = null,
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