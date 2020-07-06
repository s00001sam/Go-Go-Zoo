package com.sam.gogozoo.homepage

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.Control
import com.sam.gogozoo.databinding.HomeFragmentBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.data.MockData

class HomeFragment : Fragment(){

    private val viewModel by viewModels<HomeViewModel> { getVmFactory() }
    lateinit var binding: HomeFragmentBinding
    lateinit var mapFragment: SupportMapFragment
    //create some variables for address
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        fun newInstance() = HomeFragment()
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ZooApplication.appContext)
        getLastLocation()

        //back to zoo
        binding.buttonBack.setOnClickListener {
            mapFragment.getMapAsync(viewModel.callback1)
        }
        //clear all polyline
        binding.buttonClear.setOnClickListener {
            Control.hasPolyline = false
            viewModel.clearPolyline()
            mapFragment.getMapAsync(viewModel.callback1)
        }

        //show info dialog
        viewModel.info.observe(viewLifecycleOwner, Observer {
            Log.d("sam","navInfo=$it")
            Handler().postDelayed(Runnable {
                this.findNavController().navigate(HomeFragmentDirections.actionGlobalInfoDialog(it))
            }, 600L)
        })

        //direction to selected marker
        (activity as MainActivity).needNavigation.observe(viewLifecycleOwner, Observer {
            viewModel.clearPolyline()
            val location1 = viewModel.myLatLng.value
            val location2 = viewModel.navLatLng.value
            Log.d("sam","hasPoly=${Control.hasPolyline}")
            if (Control.hasPolyline == false) {
                location1?.let {
                    mapFragment.getMapAsync(
                        viewModel.directionCall(it, location2 ?: it, getString(R.string.google_maps_key)
                        )
                    )
                }
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(activity as MainActivity)
            mapFragment.getMapAsync(viewModel.allMarks)
            mapFragment.getMapAsync(markerCall)
    }

    //get now LagLng of location
    private fun getLastLocation(){
        if ((activity as MainActivity).checkPermission()){
            //if location service is enable
            if ((activity as MainActivity).isLocationEnable()){
                //let's get the location
                fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                    var location = it.result
                    if (location == null){
                        viewModel.getNewLocation()
                    }else{
                        Log.d("sam", "sam1234${location.latitude}, ${location.longitude}")
                        viewModel.myLatLng.value = LatLng(location.latitude, location.longitude)
                    }
                }
            }else{
                Toast.makeText(ZooApplication.appContext, "Please enble your location service", Toast.LENGTH_SHORT).show()
            }
        }else{
            (activity as MainActivity).checkPermission()
        }
    }

    val markerCall = OnMapReadyCallback { googleMap ->
        googleMap.setOnMarkerClickListener {
            Log.d("sam","marker=${it.title}")
            viewModel.navLatLng.value = it.position
            val list = MockData.animals.filter { info ->
                info.title == it.title
            }
            val areaList = MockData.areas.filter { info ->
                info.title == it.title
            }
            var image = 0
            list.forEach {info ->
                image = info.drawable
            }
            areaList.forEach {
                image = it.drawable
            }
            val location = "( " + it.position.longitude + ", " + it.position.latitude + " )"
            viewModel.info.value = NavInfo(it.title, location, image)
            Control.hasPolyline = false

            false
        }
    }

}
