package com.example.favouriteplaces

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var flMain: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flMain = findViewById(R.id.flMain)
        val bottomNavBar: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val db = Firebase.firestore
        checkIfUserIsLoggedIn(StartFragment(), LoginFragment())
        val user = User("")


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

    private fun checkIfUserIsLoggedIn(loggedInFragment: Fragment, notLoggedInFragment: Fragment) {
        val auth = Firebase.auth

        if(auth.currentUser == null) {
            switchFragment(notLoggedInFragment)
        }else {
            switchFragment(loggedInFragment)
        }
    }

    fun switchFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flMain, fragment).commit()
    }
}