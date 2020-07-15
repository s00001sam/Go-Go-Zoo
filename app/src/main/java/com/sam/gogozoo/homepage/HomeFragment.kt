package com.sam.gogozoo.homepage

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.angads25.toggle.interfaces.OnToggledListener
import com.github.angads25.toggle.model.ToggleableView
import com.github.angads25.toggle.widget.LabeledSwitch
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.Control
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.Schedule
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.databinding.HomeFragmentBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.getDinstance
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : Fragment(), OnToggledListener{

    private val viewModel by viewModels<HomeViewModel> { getVmFactory() }
    lateinit var binding: HomeFragmentBinding
    lateinit var mapFragment: SupportMapFragment
    //create some variables for address
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    //bottomSheet
    lateinit var bottomBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var bottomSheet: View
    lateinit var labeledSwitch: LabeledSwitch

    companion object {
        fun newInstance() = HomeFragment()
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.context.value = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ZooApplication.appContext)
        getLastLocation()
        val speedDialView = binding.speedDial
        initSpeedbutton()

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

        binding.buttonMyLocation.setOnClickListener {
            mapFragment.getMapAsync(viewModel.myLocationCall)
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
            viewModel.clearMarker()
            viewModel.clearPolyline()
            val location1 = viewModel.myLatLng.value
            val location2 = (activity as MainActivity).info.value?.latLng
            Log.d("sam","hasPoly=${Control.hasPolyline}")
            if (Control.hasPolyline == false) {
                location1?.let {
                    mapFragment.getMapAsync(viewModel.directionCall(it, location2 ?: it))
                    mapFragment.getMapAsync(viewModel.onlyMoveCamera(it, 18f))
                }
            }
        })

        (activity as MainActivity).selectRoute.observe(viewLifecycleOwner, Observer {
            Logger.d("selectRoute=$it")
            viewModel.selectSchedule.value = it
            (binding.rcySchedule.adapter as ScheduleAdapter).notifyDataSetChanged()
        })

        val facAdapter = HomeFacAdapter(viewModel)
        binding.rcyFacility.adapter = facAdapter

        (activity as MainActivity).selectFacility.observe(viewLifecycleOwner, Observer {
            if (it != listOf<LocalFacility>()) {
                it?.forEach {
                    it.meter = it.geo[0].getDinstance(MockData.myLocation)
                    viewModel.clearMarker()
                    mapFragment.getMapAsync(viewModel.onlyAddMark(it.geo[0], it.name))
                }
                (binding.rcyFacility.adapter as HomeFacAdapter).submitList(it)
                Control.hasPolyline = false
                viewModel.clickMark.value?.hideInfoWindow()
                binding.rcyFacility.visibility = View.VISIBLE
            }
        })

        viewModel.selectFac.observe(viewLifecycleOwner, Observer {
            mapFragment.getMapAsync(viewModel.onlyMoveCamera(it.geo[0], 18f))
            val isAnimal = MockData.localAnimals.filter {animal -> animal.nameCh == it.name }
            if (isAnimal == listOf<LocalAnimal>()) {
                (activity as MainActivity).info.value = NavInfo(it.name, it.geo[0], image = R.drawable.icon_house)
            }else if (it.imageUrl == ""){
                (activity as MainActivity).info.value = NavInfo(it.name, it.geo[0], image = R.drawable.image_placeholder)
            }else{
                (activity as MainActivity).info.value = NavInfo(it.name, it.geo[0], imageUrl = it.imageUrl)
            }
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
                MockData.myLocation = it
                Log.d("sam","mylocation=${MockData.myLocation}")
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

        val scheduleAdapter = ScheduleAdapter(viewModel)
        binding.rcySchedule.adapter = scheduleAdapter

        viewModel.selectSchedule.observe(viewLifecycleOwner, Observer {
            Logger.d("selectSchedule0000=$it")

            viewModel.clearMarker()
            viewModel.clearPolyline()
            showBottomSheet()
            if (it.list != listOf<NavInfo>()) {
                binding.textNoRoute.visibility = View.GONE
                binding.imageNoRoute.visibility =View.GONE
                mapFragment.getMapAsync(
                    viewModel.onlyMoveCamera(it.list[0].latLng, 16f)
                )
                for (i in it.list) {
                    i.meter = i.latLng.getDinstance(MockData.myLocation)
                    mapFragment.getMapAsync(viewModel.onlyAddMark(i.latLng, i.title))
                }
                val sortList = it.list.sortedBy { it.meter }
                Logger.d("sortlist=$sortList")
                (binding.rcySchedule.adapter as ScheduleAdapter).submitList(sortList)
            }else{
                (binding.rcySchedule.adapter as ScheduleAdapter).submitList(it.list)
                binding.textNoRoute.visibility = View.VISIBLE
                binding.imageNoRoute.visibility =View.VISIBLE
            }
        })

        viewModel.deleteNavInfo.observe(viewLifecycleOwner, Observer {nav ->
            viewModel.selectSchedule.value?.let {schedule ->
                val list = schedule.list.toMutableList()
                Logger.d("listnav=$list")
                list.remove(nav)
                viewModel.selectSchedule.value = Schedule(schedule.name, list)

                MockData.schedules.forEach {
                    if (it.name == schedule.name){
                        it.list = list
                    }
                }
                (binding.rcySchedule.adapter as ScheduleAdapter).notifyDataSetChanged()
            }
        })

        viewModel.edit.observe(viewLifecycleOwner, Observer {
            Logger.d("editvalue=$it")
            if (it == true) {
                binding.buttonEdit.visibility = View.GONE
                binding.buttonConfirm.visibility = View.VISIBLE
            }
            if (it == false) {
                binding.buttonEdit.visibility = View.VISIBLE
                binding.buttonConfirm.visibility = View.GONE
            }

            (binding.rcySchedule.adapter as ScheduleAdapter).notifyDataSetChanged()
        })

        viewModel.selectRoutePosition.observe(viewLifecycleOwner, Observer {
            viewModel.clearPolyline()
            mapFragment.getMapAsync(viewModel.myLocationCall)
            mapFragment.getMapAsync(viewModel.directionCall(viewModel.myLatLng.value, it.latLng))
            collapseBottomSheet()
        })

        binding.switchMarkers.setOnToggledListener(this)


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
//             val locationButton= (mapView?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))
//             val rlp=locationButton.layoutParams as (RelativeLayout.LayoutParams)
//                // position on right bottom
//                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
//                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
//                rlp.setMargins(0,0,30,30)
            }

    }

    override fun onStart() {
        super.onStart()
        bottomBehavior = BottomSheetBehavior.from(bottom_dialog)
        hideBottomSheet()
        bottomBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        viewModel.confirm()
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                }
            }
        })
    }


    fun showBottomSheet() {
        bottomBehavior.isHideable=false
        setBottomViewVisible(bottomBehavior.state != BottomSheetBehavior.STATE_EXPANDED)
    }

    fun collapseBottomSheet(){
        setBottomViewVisible(bottomBehavior.state != BottomSheetBehavior.STATE_COLLAPSED)
    }

    fun  hideBottomSheet(){
        bottomBehavior.isHideable=true
        bottomBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onDestroyView() {
        Logger.d("destroy")
        super.onDestroyView()
        (activity as MainActivity).info.value = NavInfo()
        (activity as MainActivity).markInfo.value = NavInfo()
        (activity as MainActivity).selectFacility.value = listOf()
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

            val filterAnimal = MockData.localAnimals.filter { animal -> animal.nameCh == it.title }
            val filterArea = MockData.localAreas.filter { area -> area.name == it.title }
            val filterFac = MockData.localFacility.filter { facility-> facility.name == it.title }
            var image = 0
            var imageUrl = ""
            if (filterAnimal != listOf<LocalAnimal>()){
                if (filterAnimal[0].pictures != listOf<String>()) {
                    imageUrl = filterAnimal[0].pictures[0]
                }else{
                    image = R.drawable.image_placeholder
                }
            }else if (filterArea != listOf<LocalArea>()){
                imageUrl = filterArea[0].picture
            }else if (filterFac != listOf<LocalFacility>()){
                image = R.drawable.icon_house
            }else
                image = 0

            val location = LatLng(it.position.latitude, it.position.longitude)
            (activity as MainActivity).info.value = NavInfo(it.title, location, image = image, imageUrl = imageUrl)
            Control.hasPolyline = false
            binding.rcyFacility.visibility = View.GONE

            false
        }
    }

    private fun setBottomViewVisible(showFlag: Boolean) {

        if (showFlag)
            bottomBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        else
            bottomBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun initSpeedbutton(){
        val speedDialView = binding.speedDial

        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_clear, R.drawable.icon_clear)
                .setFabBackgroundColor(resources.getColor(R.color.end_color))
                .setLabel(getString(R.string.fab_clear))
                .setLabelColor(Color.BLACK)
                .setLabelBackgroundColor(resources.getColor(R.color.end_color))
                .setLabelClickable(true)
                .create())
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_friend, R.drawable.icon_frined)
                .setFabBackgroundColor(resources.getColor(R.color.end_color))
                .setLabel(getString(R.string.fab_friend))
                .setLabelColor(Color.BLACK)
                .setLabelBackgroundColor(resources.getColor(R.color.end_color))
                .setLabelClickable(true)
                .create())
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_schedule, R.drawable.icon_cat)
                .setFabBackgroundColor(resources.getColor(R.color.end_color))
                .setLabel(getString(R.string.fab_route))
                .setLabelColor(Color.BLACK)
                .setLabelBackgroundColor(resources.getColor(R.color.end_color))
                .setLabelClickable(true)
                .create())

        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_clear -> {
                    Log.d("sam","sam_fab clear")
                    viewModel.clearMarker()
                    viewModel.clearPolyline()
                    mapFragment.getMapAsync(viewModel.callback1)
                    binding.rcyFacility.visibility = View.GONE
                    hideBottomSheet()
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_friend -> {
                    Log.d("sam","sam_fab friend")
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_schedule -> {
                    Log.d("sam","sam_fab schedule")
                    viewModel.showSelectAlert()
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
            }
            false
        })

        speedDialView.setOnChangeListener(object : SpeedDialView.OnChangeListener {
            override fun onMainActionSelected(): Boolean {
                Log.d("sam","onMainActionSelected")
                return false // True to keep the Speed Dial open
            }

            override fun onToggleChanged(isOpen: Boolean) {
                Log.d("sam","onToggleChanged")
            }
        })
    }

    override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
        if(isOn) {
            mapFragment.getMapAsync(viewModel.allMarks)
        } else {
            Logger.d("clearOk")
            viewModel.clearOriMarkers()
        }
    }
}
