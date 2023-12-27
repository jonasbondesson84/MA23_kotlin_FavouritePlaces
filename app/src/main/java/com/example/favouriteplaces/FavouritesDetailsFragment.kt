package com.example.favouriteplaces

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
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
 * Use the [FavouritesDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouritesDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var tvTitle: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvReview: TextView
    private lateinit var tvCategory: TextView
    private lateinit var rbStar: RatingBar
    private lateinit var btnLocation: ImageButton
    private var currentPlace: Place? = null


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
        val view = inflater.inflate(R.layout.fragment_favourites_details, container, false)
        db = Firebase.firestore
        auth = Firebase.auth
        tvTitle = view.findViewById(R.id.tvDetailsTitle)
        tvDesc = view.findViewById(R.id.tvDetailsDescription)
        tvCategory = view.findViewById(R.id.tvDetailsCatecory)
        tvReview = view.findViewById(R.id.tvDetailsReview)
        rbStar = view.findViewById(R.id.rbDetailsStars)
        btnLocation = view.findViewById(R.id.imbDetailsLocation)



        hideAllElements()
        if(param1 != null) {
            getPlace()
        }

        btnLocation.setOnClickListener {
            if(currentPlace != null) {

                currentPlace!!.lat?.let {it1 ->
                    currentPlace!!.lng?.let {it2 ->
                        (activity as MainActivity).switchFragment(
                            MapsFragment2.newInstance(
                                it1,
                                it2,
                                false
                            )
                        )
                    }
                }
                }
            }


        return view
    }

    private fun getPlace() {
        val user = currentUser

        if(user != null) {
            for(place in currentUser.favouritesList) {
                if (place.docID == param1) {
                    currentPlace = place
                    showElements(place)
                }
            }
            val place = currentUser.favouritesList
//            db.collection("users").document(user.userID.toString()).collection("favourites").document(param1.toString()).get()
//                .addOnSuccessListener {
//                    Log.d("!!!", "yes")
//                }
        }
    }
    private fun hideAllElements() {
        tvDesc.visibility = View.INVISIBLE
        tvCategory.visibility = View.INVISIBLE
        tvReview.visibility = View.INVISIBLE
        rbStar.visibility = View.INVISIBLE
    }

    private fun showElements(place: Place){
        tvTitle.text = place.title

        if (place.description != null) {
            tvDesc.visibility = View.VISIBLE
            tvDesc.text = place.description
        }
        if (place.category != null) {
            tvCategory.text = place.category
            tvCategory.visibility = View.VISIBLE
        }
        if(place.review != null) {
            tvReview.text = place.review
            tvReview.visibility = View.VISIBLE
        }
        if(place.stars != null) {
            rbStar.visibility = View.VISIBLE
            rbStar.rating = place.stars
            Log.d("!!!", place.stars.toString())
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavouritesDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouritesDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}