package com.teheft.storyapp.ui.map

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.teheft.storyapp.R
import com.teheft.storyapp.data.Result
import com.teheft.storyapp.data.pref.dataStore
import com.teheft.storyapp.data.remote.response.ListStoryItem
import com.teheft.storyapp.ui.home.HomeViewModel
import com.teheft.storyapp.utils.ViewModelFactory

class MapsFragment : Fragment() {

    private val mapsViewModel by viewModels<MapsViewModel> {
        context?.let { ViewModelFactory.getInstance(it, it.dataStore) }!!
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        getMarkedLocation(googleMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.nav_map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun getMarkedLocation(googleMap: GoogleMap) {
        mapsViewModel.getStories().observe(viewLifecycleOwner){result ->
            when(result){
                is Result.Loading -> {}
                is Result.Success -> {
                    putMarkedLocation(result.data, googleMap)
                }
                is Result.Error -> {
                    Toast.makeText(context, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

   fun putMarkedLocation(data: List<ListStoryItem?>?, googleMap: GoogleMap) {
        googleMap.clear()

       data?.forEach{ location ->
           val latLng = LatLng(location?.lat!!, location.lon!!)
           googleMap.addMarker(
               MarkerOptions()
                   .position(latLng)
                   .title(location.name)
                   .snippet(location.description)
           )
           boundsBuilder.include(latLng)
       }

       val bounds: LatLngBounds = boundsBuilder.build()
       googleMap.animateCamera(
           CameraUpdateFactory.newLatLngBounds(
               bounds,
               resources.displayMetrics.widthPixels,
               resources.displayMetrics.heightPixels,
               300
           )
       )
    }
}