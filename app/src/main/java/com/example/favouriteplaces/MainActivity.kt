package com.example.favouriteplaces

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var flMain: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flMain = findViewById(R.id.flMain)
        val bottomNavBar: BottomNavigationView = findViewById(R.id.bottom_navigation)

        switchFragment(StartFragment())

        bottomNavBar.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    switchFragment(StartFragment())
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
                    switchFragment(AccountFragment())
                    true
                }
            }
        }





    }

    fun switchFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flMain, fragment).commit()
    }
}