package com.example.favouriteplaces

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
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
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var etvName: EditText
    private lateinit var etvLocation: EditText

    private lateinit var btnSave: Button

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
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        auth = Firebase.auth
        db = Firebase.firestore
        btnSave = view.findViewById(R.id.btnSave)
        etvLocation = view.findViewById(R.id.etvAccountLocation)
        etvName = view.findViewById(R.id.etcAccountName)
        val topAppBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)

        btnSave.setOnClickListener {
            val name = etvName.text.toString()
            val location = etvLocation.text.toString()
            Log.d("!!!", currentUser.documentId.toString())
            if(currentUser.userID != null) {
                db.collection("usersCollection").document(currentUser.documentId.toString())
                    .update("name", name)
                    .addOnCompleteListener {task ->
                        if(task.isSuccessful) {
                            Log.d("!!!", "got here")
                            (activity as MainActivity).switchFragment(StartFragment())
                        }

                    }
            }

        }
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuLogoutAccount -> {
                    signOut()
                    true
                }

                else -> false
            }
        }


        return view
    }
    private fun signOut() {
        currentUser.resetUser()

        auth.signOut()
        (activity as MainActivity).switchFragment(LoginFragment())
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}