package com.sam.gogozoo.homepage

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.angads25.toggle.interfaces.OnToggledListener
import com.github.angads25.toggle.model.ToggleableView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.R
import com.sam.gogozoo.data.*
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.databinding.HomeFragmentBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.getDinstance
import kotlinx.android.synthetic.main.home_fragment.*
import com.sam.gogozoo.util.Util.getEmailName


class HomeFragment : Fragment(), OnToggledListener{

    private val viewModel by viewModels<HomeViewModel> { getVmFactory() }
    lateinit var binding: HomeFragmentBinding
    lateinit var mapFragment: SupportMapFragment
    //bottomSheet
    lateinit var bottomBehavior: BottomSheetBehavior<ConstraintLayout>

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
//        getLastLocation()
        val speedDialView = binding.speedDial
        initSpeedbutton()
        viewModel.getNewLocation()

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
                    it.meter = it.geo[0].getDinstance(UserManager.user.geo)
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
                UserManager.user.geo = it
                Log.d("sam","mylocation=${it}")
                viewModel.publishUser(UserManager.user)
            }
            viewModel.needFriendLocation()
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
                    i.meter = i.latLng.getDinstance(UserManager.user.geo)
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
            mapFragment.getMapAsync(viewModel.onlyAddMark(it.latLng, it.title))
            mapFragment.getMapAsync(viewModel.onlyMoveCamera(it.latLng, 19f))
            (activity as MainActivity).info.value = it
            Logger.d("info=${(activity as MainActivity).info.value}")
            Control.hasPolyline = false
            collapseBottomSheet()
        })

        viewModel.user.observe(viewLifecycleOwner, Observer {
            viewModel.publishFriend(UserManager.user.email, it)
        })

        val friendAdapter = FriendAdapter(viewModel)
        binding.rcyFriends.adapter = friendAdapter

        viewModel.liveFriend.observe(viewLifecycleOwner, Observer {
            UserManager.friends = it
            Logger.d("livefriends=${UserManager.friends}")
            (binding.rcyFriends.adapter as FriendAdapter).submitList(it)
        })

        viewModel.friendLocation.observe(viewLifecycleOwner, Observer {
            UserManager.friends = it
            Logger.d("livefriends=${UserManager.friends}")
            Logger.d("visibleFriend=${viewModel.visibleFriend}")
            if (viewModel.visibleFriend > 0){
                viewModel.clearFriendMarker()
                it.forEach {user ->
                    mapFragment.getMapAsync(viewModel.onlyAddMarkFriend(user.geo, user.email))
                }
            }
        })

        viewModel.selectFriend.observe(viewLifecycleOwner, Observer {user ->
            Logger.d("selectFriend=$user")
            val list = UserManager.friends.filter { it.email == user.email }
            Logger.d("filterfriends=$list")
            if (list != listOf<User>()) {
                mapFragment.getMapAsync(viewModel.onlyMoveCamera(list[0].geo, 18f))
                mapFragment.getMapAsync(viewModel.onlyAddMarkFriend(list[0].geo, list[0].email.getEmailName()))
                (activity as MainActivity).info.value =
                    NavInfo(title = list[0].email.getEmailName(), latLng = list[0].geo, imageUrl = list[0].picture)
                Control.hasPolyline = false
                binding.rcyFacility.visibility = View.GONE
            }
        })

        binding.switchMarkers.setOnToggledListener(this)

        binding.buttonRefresh.setOnClickListener {
            viewModel.selectSchedule.value = viewModel.selectSchedule.value
        }
        binding.buttonEarser.setOnClickListener {
            viewModel.clearPolyline()
        }

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
                        binding.buttonEarser.visibility = View.GONE
                        binding.buttonRefresh.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.buttonEarser.visibility = View.VISIBLE
                        binding.buttonRefresh.visibility = View.VISIBLE
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

    val markerCall = OnMapReadyCallback { googleMap ->
        googleMap.setOnMarkerClickListener {
            Log.d("sam","marker=${it.title}")
            viewModel.clickMark.value = it
            viewModel.navLatLng.value = it.position

            val filterAnimal = MockData.localAnimals.filter { animal -> animal.nameCh == it.title }
            val filterArea = MockData.localAreas.filter { area -> area.name == it.title }
            val filterFac = MockData.localFacility.filter { facility-> facility.name == it.title }
            val filterFriend = UserManager.friends.filter {friend -> friend.email == it.title }
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
            }else if (filterFriend != listOf<User>()){
                imageUrl = filterFriend[0].picture
            }else
                image = 0

            val location = LatLng(it.position.latitude, it.position.longitude)
            (activity as MainActivity).info.value = NavInfo(it.title.getEmailName(), location, image = image, imageUrl = imageUrl)
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
                    showFriends()
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

    fun showFriends(){
        if (viewModel.visibleFriend > 0){
            UserManager.friends.forEach {
                mapFragment.getMapAsync(viewModel.onlyAddMarkFriend(it.geo, it.email))
            }
            binding.rcyFriends.visibility = View.VISIBLE
            mapFragment.getMapAsync(viewModel.onlyMoveCamera(UserManager.friends[0].geo, 16.5f))
        }else{
            viewModel.clearFriendMarker()
            binding.rcyFriends.visibility = View.GONE
        }
        viewModel.visibleFriend = (viewModel.visibleFriend)*(-1)
    }
}
