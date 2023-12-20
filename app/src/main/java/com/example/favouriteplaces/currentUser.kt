package com.example.favouriteplaces

object currentUser {
               var documentId : String? = null
               var userID: String? = null
               var name: String? = null
               var location: String? = null
               var userImage: String? = null

    var favouritesList = mutableListOf <Place>()

    init {
        favouritesList.add(Place("Hemma", description = "Här är det bäst"))
        favouritesList.add(Place("Borta", description = "Här är det sämst"))
    }


}