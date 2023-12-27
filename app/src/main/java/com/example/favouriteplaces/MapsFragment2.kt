package com.example.favouriteplaces

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar


private const val ARG_PARAM1 = "lat"
private const val ARG_PARAM2 = "lng"
private const val ARG_PARAM3 = "setLatLng"

class MapsFragment2 : Fragment() , OnMapReadyCallback{
    private var lat: Double? = null
    private var lng: Double? = null
    private var setLatLng: Boolean? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()


//    private val callback = OnMapReadyCallback { googleMap ->
//        /**
//         * Manipulates the map once available.
//         * This callback is triggered when the map is ready to be used.
//         * This is where we can add markers or lines, add listeners or move the camera.
//         * In this case, we just add a marker near Sydney, Australia.
//         * If Google Play services is not installed on the device, the user will be prompted to
//         * install it inside the SupportMapFragment. This method will only be triggered once the
//         * user has installed Google Play services and returned to the app.
//         */
//
//        val sthlm = LatLng(59.334591, 18.063240)
//       // googleMap.addMarker(MarkerOptions().position(sthlm).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sthlm))
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lat = it.getDouble(ARG_PARAM1)
            lng = it.getDouble(ARG_PARAM2)
            setLatLng = it.getBoolean(ARG_PARAM3)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps2, container, false)

        getLatLang(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        setMarkers()


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun setMarkers() {
        for(place in currentUser.favouritesList) {
            createMark(place)
        }
    }

    private fun createMark(place: Place) {

    }

    private fun getLatLang(view: View) {
        if (lat != null && lng != null) {
            Snackbar.make(view, "lat: $lat lng: $lng", 2000).show()
            Log.d("!!!", "lat: $lat lng: $lng")


        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param lat Parameter 1.
         * @param lng Parameter 2.
         * @return A new instance of fragment FavouriteFragments.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(lat: Double, lng: Double, setLatLng: Boolean) =
            MapsFragment2().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_PARAM1, lat)
                    putDouble(ARG_PARAM2, lng)
                    putBoolean(ARG_PARAM3, setLatLng)
                }
            }
    }

    override fun onMapReady(map: GoogleMap) {
        val adapter = PlacesInfoAdapter(requireContext())
        map.setInfoWindowAdapter(adapter)

        map.setOnMapClickListener {
            if(setLatLng != null) {
                if(setLatLng as Boolean) {
                    sharedViewModel.setLocation(it)

                    closeFragment()
                }
            }
        }

        if (lat != null && lng != null) {
           map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat!!, lng!!), 15f))

        } else {
            val sthlm = LatLng(59.334591, 18.063240)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sthlm, 15f))
        }
        for(place in currentUser.favouritesList) {
             place.lat?.let {it1 ->
                 place.lng?.let {it2 ->
                     val position = LatLng(it1, it2)
                     val marker = map.addMarker(MarkerOptions().position(position).title(place.title))
                     marker?.tag = place
                 }
             }

        }
    }



    private fun closeFragment() {
        val activity = activity
        if(activity != null && isAdded) {

            val fragmentManager = activity.supportFragmentManager
            if(fragmentManager.backStackEntryCount > 0 ) {
                fragmentManager.popBackStack()
            } else {
                fragmentManager.beginTransaction().remove(this).commit()
            }
        }
    }



}

