package com.example.favouriteplaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFavouriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFavouriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btnSave: Button
    private lateinit var etvTitle: EditText
    private lateinit var etvDescription: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_favourite, container, false)

        btnSave = view.findViewById(R.id.btnAddSave)
        etvDescription = view.findViewById(R.id.etvAddDesc)
        etvTitle = view.findViewById(R.id.etvAddTitle)
        auth = Firebase.auth
        db = Firebase.firestore

        btnSave.setOnClickListener {
            saveItem(it)
        }

        return view
    }
    private fun saveItem(view: View) {
        val title = etvTitle.text.toString()
        val description = etvDescription.text.toString()
        val user = currentUser

        if(title.isEmpty() || user.userID == null) {
            return
        } else {
            val place = Place(title = title, description = description)
            db.collection("users").document(user.userID.toString()).collection("favourites").add(place)
                .addOnCompleteListener {task ->
                    if(task.isSuccessful) {
                        (activity as MainActivity).switchFragment(FavouriteFragment())
                    } else {
                        Snackbar.make(view, "Error", 2000).show()
                    }
                }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFavouriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFavouriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}