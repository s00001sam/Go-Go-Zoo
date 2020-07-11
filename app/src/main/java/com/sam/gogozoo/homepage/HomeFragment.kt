package com.sam.gogozoo.homepage

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.Control
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.databinding.HomeFragmentBinding
import com.sam.gogozoo.ext.getVmFactory
import kotlinx.android.synthetic.main.activity_main.*
import com.sam.gogozoo.util.Util.getDinstance


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
            viewModel.clearMarker()
            mapFragment.getMapAsync(viewModel.callback1)
        }

        //show info dialog
        (activity as MainActivity).info.observe(viewLifecycleOwner, Observer {
            Log.d("sam","navInfo=$it")
            if (it != NavInfo()) {
                viewModel.clickMark.value?.hideInfoWindow()
                Handler().postDelayed(Runnable {
                    this.findNavController()
                        .navigate(HomeFragmentDirections.actionGlobalInfoDialog(it))
                }, 800L)
            }
        })

        (activity as MainActivity).markInfo.observe(viewLifecycleOwner, Observer {
            viewModel.clearMarker()
            if (it != NavInfo()) {
                binding.rcyFacility.visibility = View.GONE
                mapFragment.getMapAsync(viewModel.markCallback1(it.latLng, it.title))
            }
        })

        //direction to selected marker
        (activity as MainActivity).needNavigation.observe(viewLifecycleOwner, Observer {
            viewModel.clearPolyline()
            val location1 = viewModel.myLatLng.value
            val location2 = (activity as MainActivity).info.value?.latLng
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

        val facAdapter = HomeFacAdapter(viewModel)
        binding.rcyFacility.adapter = facAdapter

        (activity as MainActivity).selectFacility.observe(viewLifecycleOwner, Observer {
            it?.forEach {
                viewModel.clearMarker()
                mapFragment.getMapAsync(viewModel.onlyAddMark(it.geo[0], it.name))
            }
            (binding.rcyFacility.adapter as HomeFacAdapter).submitList(it)
            Control.hasPolyline = false
            viewModel.clickMark.value?.hideInfoWindow()
            binding.rcyFacility.visibility = View.VISIBLE
        })

        viewModel.selectFac.observe(viewLifecycleOwner, Observer {
            mapFragment.getMapAsync(viewModel.onlyMoveCamera(it.geo[0]))
            (activity as MainActivity).info.value = NavInfo(it.name, it.geo[0], image = R.drawable.icon_house)
        })

        viewModel.myLatLng.observe(viewLifecycleOwner, Observer {
            it?.let {
                MockData.allMarkers.forEach {navInfo ->
                    navInfo.meter = navInfo.latLng.getDinstance(it)
                }
                MockData.localFacility.forEach {facility ->
                    facility.meter = facility.geo[0].getDinstance(it)
                }
                MockData.localAreas.forEach {area ->
                    area.meter = area.geo[0].getDinstance(it)
                }
            }
        })

        //set up top recycleView
        binding.rcyHomeTop.adapter = HomeTopAdapter(viewModel)
        (binding.rcyHomeTop.adapter as HomeTopAdapter).submitList(MockData.mapTopItem)

        viewModel.selectTopItem.observe(viewLifecycleOwner, Observer {string ->
            Log.d("sam", "selectTopItem=$string")
            val list = MockData.listMapTopItem.filter { it.category == string }
            val selectFacility = list[0]
            this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFacilityDialog(selectFacility))
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
         mapFragment.getMapAsync(activity as MainActivity)
         mapFragment.getMapAsync(viewModel.allMarks)
         mapFragment.getMapAsync(markerCall)
        //移動我的位置按鈕到右下角
         mapFragment.getMapAsync {
             val mapView = mapFragment.view
             val locationButton= (mapView?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))
             val rlp=locationButton.layoutParams as (RelativeLayout.LayoutParams)
                // position on right bottom
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
                rlp.setMargins(0,0,30,30)
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).info.value = NavInfo()
        (activity as MainActivity).markInfo.value = NavInfo()
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
            viewModel.clickMark.value = it
            viewModel.navLatLng.value = it.position
            val list = MockData.animals.filter { info ->
                info.title == it.title
            }
            val areaList = MockData.areas.filter { info ->
                info.title == it.title
            }
            val facList = MockData.listFotFacImage.filter {string ->
                string == it.title
            }
            Log.d("sam", "facList=$facList")
            var image = 0
            if (facList == listOf<String>()) {
                list.forEach { info ->
                    image = info.drawable
                    viewModel.clearMarker()
                }
                areaList.forEach {
                    image = it.drawable
                    viewModel.clearMarker()
                }
            }else{
                image = R.drawable.icon_house
            }
            val location = LatLng(it.position.latitude, it.position.longitude)
            (activity as MainActivity).info.value = NavInfo(it.title, location, image = image)
            Control.hasPolyline = false
            binding.rcyFacility.visibility = View.GONE

            false
        }
    }
}
