package com.example.favouriteplaces

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFavouriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFavouriteFragment : Fragment(),  AdapterView.OnItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btnSave: Button
    private lateinit var etvTitle: EditText
    private lateinit var etvDescription: EditText
    private lateinit var btnTakeImage: FloatingActionButton
    private lateinit var btnSelectImage: FloatingActionButton
    private lateinit var imAddImage: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var spCategory: Spinner
    private lateinit var rbStars: RatingBar
    private lateinit var etvReview: EditText
    private lateinit var tbSharePlace: ToggleButton
    private lateinit var btnGetLocation: ImageButton
    private lateinit var tvAddLatLng: TextView

    private var latitude: Double? = null
    private var longitude: Double? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val TAKE_PHOTO_CODE = 1
    private val SELECT_PHOTO_CODE = 2
    private var ACTION_CODE =0

    private var activityResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { result ->
            var allAreGranted = true
            for(b in result.values) {
                allAreGranted = allAreGranted && b
            }

            if(allAreGranted) {
                if(ACTION_CODE == TAKE_PHOTO_CODE) {
                    takePhoto()
                } else if(ACTION_CODE == SELECT_PHOTO_CODE) {
                    selectPhoto()
                }

            }
        }


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
        btnTakeImage = view.findViewById(R.id.fabAddTakeImage)
        btnSelectImage = view.findViewById(R.id.fabAddSelectImage)
        imAddImage = view.findViewById(R.id.imAddImage)
        spCategory = view.findViewById(R.id.spCategory)
        rbStars = view.findViewById(R.id.rbStars)
        etvReview = view.findViewById(R.id.etvReview)
        tbSharePlace = view.findViewById(R.id.tbSharePlace)
        btnGetLocation = view.findViewById(R.id.imbAddLocation)
        tvAddLatLng = view.findViewById(R.id.tvAddLatLng)
        auth = Firebase.auth
        db = Firebase.firestore
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spCategory.adapter = adapter
        }

        spCategory.onItemSelectedListener = this





        btnSave.setOnClickListener {
            saveItem(it)
        }

        btnGetLocation.setOnClickListener {
            (activity as MainActivity).startNewFragmentForSetLocation()
        }

        btnTakeImage.setOnClickListener {
            ACTION_CODE = TAKE_PHOTO_CODE
            val appPerms = arrayOf(
                android.Manifest.permission.CAMERA
            )
            activityResultLauncher.launch(appPerms)

        }

        btnSelectImage.setOnClickListener {
            ACTION_CODE = SELECT_PHOTO_CODE
            val appPerms = arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
            activityResultLauncher.launch(appPerms)
//            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
//            startActivityForResult(intent, 456)
        }

        return view
    }

    override fun onResume() {
        super.onResume()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == TAKE_PHOTO_CODE) {
                val bmp = data?.extras?.get("data") as Bitmap
                imAddImage.setImageBitmap(bmp)
            } else if (requestCode == SELECT_PHOTO_CODE) {
                imAddImage.setImageURI(data?.data)
            }
        }
    }

    private fun takePhoto() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, TAKE_PHOTO_CODE)

//         Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            takePictureIntent.resolveActivity(MainActivity().packageManager)?.also {
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    Log.d("!!!", "ERROR")
//                    null
//            }
//                photoFile?.also {
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        ,
//                        "com.example.android.fileprovider",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, TAKE_PHOTO_CODE)
//                }
//            }
//
//        }
        //startActivityForResult(intent, TAKE_PHOTO_CODE)
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
//                // Create the File where the photo should go
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    // Error occurred while creating the File
//
//                    null
//                }
//                // Continue only if the File was successfully created
//                photoFile?.also {
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        requireContext(),
//                        "com.example.android.fileprovider",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, TAKE_PHOTO_CODE)
//                }
//            }
//        }
    }


    private fun createImageFile(): File? {
        // Skapa en unik filnamn baserad på tidstämpel
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }



    private fun selectPhoto() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            startActivityForResult(intent, SELECT_PHOTO_CODE)
    }

    private fun saveItem(view: View) {
        val title = etvTitle.text.toString()
        val description = if( etvDescription.text.isNotEmpty()) etvDescription.text.toString() else null
       // val category = spCategory.selectedItem.toString()

        val stars = rbStars.rating
        val review = if (etvReview.text.isNotEmpty()) etvReview.text.toString() else null
        val sharePublic = tbSharePlace.isActivated
        val user = currentUser

        if(title.isEmpty() || user.userID == null) {
            return
        } else {
            val place = Place(
                title = title,
                description = description,
                //category = category,
                stars = stars,
                review = review,
                public = sharePublic,
                lat = sharedViewModel.lat.value,
                lng = sharedViewModel.lng.value


                )
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            sharedViewModel.setCategory(parent.getItemAtPosition(position).toString())
            Log.d("!!!", sharedViewModel.category.value.toString())
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}