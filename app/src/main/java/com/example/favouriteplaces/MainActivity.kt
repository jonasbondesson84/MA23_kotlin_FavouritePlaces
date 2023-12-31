package com.example.favouriteplaces

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var flMain: FrameLayout
    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseFirestore
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUEST_LOCATION = 1
    private var lat: Double? = null
    private var lng: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        flMain = findViewById(R.id.flMain)
        val bottomNavBar: BottomNavigationView = findViewById(R.id.bottom_navigation)
        db = Firebase.firestore
        auth = Firebase.auth


        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(2000L).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

            }
        }

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION)
        }

        checkIfUserIsLoggedIn(StartFragment(), LoginFragment())



        bottomNavBar.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    checkIfUserIsLoggedIn(StartFragment(), LoginFragment())
                    true
                }
                R.id.favourite -> {
//                    val intent = Intent(this, AddfavouriteActivity::class.java)
//                    startActivity(intent)
                    switchFragment(FavouriteFragment())
                    true
                }
                R.id.map -> {
                    if(lat != null) {
                        lat?.let { it1 ->
                            lng?.let { it2 -> switchFragment(MapsFragment2.newInstance(it1, it2, false)) } }
                        true
                    } else {
                        switchFragment(MapsFragment2())
                        true
                    }
                }
                else -> {
                    checkIfUserIsLoggedIn(AccountFragment(), LoginFragment())
                    true
                }
            }
        }



    }

    override fun onResume() {
        super.onResume()
        getLastLocation()
    }








    private fun checkIfUserIsLoggedIn(loggedInFragment: Fragment, notLoggedInFragment: Fragment) {
        val auth = Firebase.auth

        if(auth.currentUser == null) {
            switchFragment(notLoggedInFragment)
        }else {
            getCurrentUserInfo()
            switchFragment(loggedInFragment)
        }
    }
    private fun getFavourites() {
        val user = currentUser
        currentUser.favouritesList.clear()
        if (user.userID == null) {
            //Snackbar.make(view, getString(R.string.mustBeSignedIn), 2000).show()
            return
        } else {
            db.collection("users").document(user.userID.toString()).collection("favourites").get()
                .addOnSuccessListener { documentSnapshot ->
                    for (document in documentSnapshot.documents) {
                        val place = document.toObject<Place>()
                        if (place != null) {
                            currentUser.favouritesList.add(place)
                        }
                    }
                }
        }


    }
    fun getShared() {
        db.collection("usersCollection").get().addOnSuccessListener { documentSnapshot ->
            for (document in documentSnapshot) {

                val user = document.get("userID").toString()
                if(user != null) {
                    db.collection("users").document(user).collection("favourites").get()
                        .addOnSuccessListener {
                            for(document in it) {
                                val place = document.toObject<Place>()
                                if(place != null  && place.public == true) {
                                    currentUser.sharedFavouritesList.add(place)
                                    //currentUser.favouritesList.add(place)
                                    Log.d("!!!", "place: ${place.docID}")
                                }
                            }
                        }
                }
            }
        }
    }

    fun switchFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flMain, fragment).commit()
        invalidateOptionsMenu()
    }

    fun startNewFragmentForSetLocation() {
        getLastLocation()
        val transaction = supportFragmentManager.beginTransaction()
        if(lat != null) {
            lat?.let { it1 ->
                lng?.let { it2 ->
                    transaction.add(R.id.flMain,
                        MapsFragment2.newInstance(it1, it2, true),
                        "getLatLng"
                    ).commit()
                }
            }
        }

    }
    private fun getCurrentUserInfo() {
        val user = auth.currentUser
        if(user != null) {
            db.collection("usersCollection").get().addOnSuccessListener {documentSnapshot ->
                for(document in documentSnapshot) {
                    if(document.get("userID").toString() == user.uid) {
                        Log.d("!!!", "Got it!")
                        val user = document.toObject<User>()
                        currentUser.name = user.name
                        currentUser.userID = user.userID
                        currentUser.userImage = user.userImage
                        currentUser.location = user.location
                        currentUser.documentId = user.documentId
                        Log.d("!!!", currentUser.documentId.toString())
                        getFavourites()
                        getShared()
                        return@addOnSuccessListener
                    }
                }
                Log.d("!!!", "user not found!")
            }
        }


        //currentUser.name =

    }
    private fun getLastLocation() {

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationProvider.lastLocation.addOnCompleteListener {task->
                if(task.isSuccessful) {
                     val location = task.result

                    if (location != null) {
                        lat = location.latitude
                        lng = location.longitude
//                        Snackbar.make(flMain, "lat: ${location.latitude} lng: ${location.longitude}", 2000).show()
                        Log.d("!!!", "lat: ${location.latitude} lng: ${location.longitude}")
                    } else {
                        val sthlm = LatLng(59.334591, 18.063240)
                        lat = sthlm.latitude
                        lng = sthlm.longitude
                        Log.d("!!!", "No location")
                    }

                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_LOCATION) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

}
