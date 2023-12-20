package com.example.favouriteplaces

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class MainActivity : AppCompatActivity() {

    private lateinit var flMain: FrameLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flMain = findViewById(R.id.flMain)
        val bottomNavBar: BottomNavigationView = findViewById(R.id.bottom_navigation)
        //val toolbar: Toolbar = findViewById(R.id.toolbar)
        db = Firebase.firestore

        val user = User("")
        auth = Firebase.auth
        topAppBar = findViewById(R.id.topAppBar)
        checkIfUserIsLoggedIn(StartFragment(), LoginFragment())
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuLogoutAccount -> {
                    signOut()
                    true
                }

                else -> false
            }
        }


        bottomNavBar.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    checkIfUserIsLoggedIn(StartFragment(), LoginFragment())
                    true
                }
                R.id.favourite -> {
                    switchFragment(FavouriteFragment())
                    true
                }
                R.id.map -> {
                    switchFragment(MapsFragment())
                    true
                }
                else -> {
                    checkIfUserIsLoggedIn(AccountFragment(), LoginFragment())
                    true
                }
            }
        }



    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.default_menu, menu)
        updateMenuBasedOnFragment(menu)
        return true
    }

    private fun updateMenuBasedOnFragment(menu: Menu) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.flMain)

        when (currentFragment) {
            AccountFragment() -> {
                menu.findItem(R.id.menuLogoutAccount)?.isVisible = true
                menu.findItem(R.id.menuAddFavourite)?.isVisible = false
            }
            FavouriteFragment() -> {
                menu.findItem(R.id.menuLogoutAccount)?.isVisible = false
                menu.findItem(R.id.menuAddFavourite)?.isVisible = true
            }
            else -> {
                menu.findItem(R.id.menuLogoutAccount)?.isVisible = false
                menu.findItem(R.id.menuAddFavourite)?.isVisible = false
            }

        }
    }

    private fun signOut() {
        currentUser.resetUser()
        auth.signOut()
        switchFragment(LoginFragment())
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

    fun switchFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flMain, fragment).commit()
        invalidateOptionsMenu()
    }
    private fun getCurrentUserInfo() {
        val user = auth.currentUser
        if(user != null) {
            db.collection("usersCollection").get().addOnSuccessListener {DocumentSnapshot ->
                for(document in DocumentSnapshot) {
                    if(document.get("userID").toString() == user.uid) {
                        Log.d("!!!", "Got it!")
                        val user = document.toObject<User>()
                        currentUser.name = user.name
                        currentUser.userID = user.userID
                        currentUser.userImage = user.userImage
                        currentUser.location = user.location
                        currentUser.documentId = user.documentId
                        Log.d("!!!", currentUser.documentId.toString())
                        return@addOnSuccessListener
                    }
                }
                Log.d("!!!", "user not found!")
            }
        }


        //currentUser.name =

    }
}
