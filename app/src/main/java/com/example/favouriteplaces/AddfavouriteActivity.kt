package com.example.favouriteplaces

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class AddfavouriteActivity : AppCompatActivity() {

    private lateinit var btnSave: Button
    private lateinit var etvTitle: EditText
    private lateinit var etvDescription: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addfavourite)

        btnSave = findViewById(R.id.btnAddSave)
        etvDescription = findViewById(R.id.etvAddDesc)
        etvTitle = findViewById(R.id.etvAddTitle)
        auth = Firebase.auth
        db = Firebase.firestore

        btnSave.setOnClickListener {
            saveItem(it)
        }
    }

    private fun saveItem(view: View) {
        val title = etvTitle.text.toString()
        val description = etvDescription.text.toString()
        val user = currentUser

        if(title.isEmpty() || user.userID == null) {
            return
        } else {
            val place = Place(title, description)
            db.collection("users").document(user.userID.toString()).collection("favourites").add(place)
                .addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    finish()
                } else {
                    Snackbar.make(view, "Error", 2000).show()
                }
            }
        }
    }
}