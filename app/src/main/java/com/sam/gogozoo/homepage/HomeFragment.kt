package com.sam.gogozoo.homepage

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
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
import com.sam.gogozoo.ZooApplication
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
import com.sam.gogozoo.util.Util.toRoute
import kotlinx.android.synthetic.main.toast.*
import kotlinx.android.synthetic.main.toast.view.*
import kotlinx.android.synthetic.main.toast.view.toastLayout


class HomeFragment : Fragment(), OnToggledListener{

    private val viewModel by viewModels<HomeViewModel> { getVmFactory(
        HomeFragmentArgs.fromBundle(requireArguments()).route
    ) }

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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val endRoute = HomeFragmentArgs.fromBundle(requireArguments()).route
        endRoute?.let {
            viewModel.selectSchedule.value = it
        }

        binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val speedDialView = binding.speedDial
        initSpeedbutton()
        //        getLastLocation()
        viewModel.getNewLocation()

        //show info dialog
        (activity as MainActivity).info.observe(viewLifecycleOwner, Observer {
            Logger.d("navInfo=$it")
            it?.let{
                viewModel.clickMark.value?.hideInfoWindow()
                viewModel.needfocus.value = false
                Handler().postDelayed(Runnable {
                    this.findNavController()
                        .navigate(HomeFragmentDirections.actionGlobalInfoDialog(it))
                }, 600L)
            }
        })

        (activity as MainActivity).markInfo.observe(viewLifecycleOwner, Observer {
            viewModel.clearMarker()
            it?.let {
                viewModel.needfocus.value = false
                binding.rcyFacility.visibility = View.GONE
                mapFragment.getMapAsync(viewModel.markCallback1(it.latLng, it.title))
            }
        })

        //direction to selected marker
        (activity as MainActivity).needNavigation.observe(viewLifecycleOwner, Observer {
            val myDistance = UserManager.user.geo.getDinstance(viewModel.mapCenter)
//            if (myDistance < 460){
                viewModel.clearPolyline()
                val location1 = viewModel.myLatLng.value
                val location2 = (activity as MainActivity).info.value?.latLng
                viewModel.directionAim.value = location2
                Logger.d("hasPoly=${Control.hasPolyline}")
                if (!Control.hasPolyline) {
                    location1?.let {
                        binding.rcyFacility.visibility = View.GONE
                        mapFragment.getMapAsync(viewModel.directionCall(it, location2 ?: it))
                        mapFragment.getMapAsync(viewModel.onlyMoveCamera(it, 18f))
                    }
                }
//            }else{
//                viewModel.toast("目前不在動物園範圍內", viewModel.viewToast)
//            }
        })

        (activity as MainActivity).selectRoute.observe(viewLifecycleOwner, Observer {
            Logger.d("selectRoute=$it")
            it?.let {
                viewModel.selectSchedule.value = it
                (binding.rcySchedule.adapter as ScheduleAdapter).notifyDataSetChanged()
            }
        })

        val facAdapter = HomeFacAdapter(viewModel)
        binding.rcyFacility.adapter = facAdapter

        (activity as MainActivity).selectFacility.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.needfocus.value = false
                it.forEach {facility ->
                    facility.meter = facility.geo[0].getDinstance(UserManager.user.geo)
                    viewModel.clearMarker()
                    mapFragment.getMapAsync(viewModel.onlyAddMark(facility.geo[0], facility.name))
                    mapFragment.getMapAsync(viewModel.callback1)
                }
                val list = it.sortedBy { it.meter }
                viewModel.newFacList.value = list
                (binding.rcyFacility.adapter as HomeFacAdapter).submitList(list)
                Control.hasPolyline = false
                viewModel.clickMark.value?.hideInfoWindow()
                Handler().postDelayed(Runnable {
                    binding.rcyFacility.visibility = View.VISIBLE
                }, 200L)
            }
        })

        (activity as MainActivity).selectNavAnimal.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.needfocus.value = false
                it.forEach {facility ->
                    facility.meter = facility.geo[0].getDinstance(UserManager.user.geo)
                    viewModel.clearMarker()
                    mapFragment.getMapAsync(viewModel.onlyAddMark(facility.geo[0], facility.name))
                }
                val list = it.sortedBy { it.meter }
                mapFragment.getMapAsync(viewModel.onlyMoveCamera(list[0].geo[0], 16f))
                Control.hasPolyline = false
                viewModel.clickMark.value?.hideInfoWindow()
            }
        })

        val linearSnapHelper = LinearSnapHelper().apply {
            attachToRecyclerView(binding.rcyFacility)
        }

        binding.rcyFacility.setOnScrollChangeListener { _, _, _, _, _ ->
            viewModel.onGalleryScrollChange(
                binding.rcyFacility.layoutManager, linearSnapHelper
            )
        }

        viewModel.snapPosition.observe(viewLifecycleOwner, Observer {position ->
            if ((activity as MainActivity).selectFacility.value != listOf<LocalFacility>()) {
                (activity as MainActivity).selectFacility.value?.let {
                    mapFragment.getMapAsync(viewModel.onlyMoveCamera(it[position].geo[0], 19f))
                }
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
                Logger.d("mylocation=${it}")
                if (Control.getPhoto) {
                    viewModel.publishUser(UserManager.user)
                }
                if (viewModel.needfocus.value == true){
                    Logger.d("camera move at me")
                    mapFragment.getMapAsync(viewModel.onlyMoveCamera(it, 19f))
                }
                if (bottomBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel.selectSchedule.value?.list?.let {list ->
                        list.forEach {
                            it.meter = it.latLng.getDinstance(UserManager.user.geo)
                        }
                        val newSortList = list.sortedBy { it.meter }
                        Logger.d("newSortList=$newSortList")
                        (binding.rcySchedule.adapter as ScheduleAdapter).submitList(newSortList)
                    }
                }
                viewModel.directionAim.value?.let {aim ->
                    if (viewModel.showRouteInfo.value == true && it.getDinstance(aim) < 20){
                        viewModel.toast(getString(R.string.text_arrive))
                        viewModel.clearPolyline()
                        Handler().postDelayed(Runnable {
                            mapFragment.getMapAsync(viewModel.callback1) }, 200L)
                    }
                }
            }
            viewModel.needFriendLocation()
        })

        //set up top recycleView
        binding.rcyHomeTop.adapter = HomeTopAdapter(viewModel)
        (binding.rcyHomeTop.adapter as HomeTopAdapter).submitList(MockData.mapTopItem)

        viewModel.selectTopItem.observe(viewLifecycleOwner, Observer {string ->
            Logger.d( "selectTopItem=$string")
            val list = MockData.listMapTopItem.filter { it.category == string }
            val selectFacility = list[0]
            this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFacilityDialog(selectFacility))
        })

        val scheduleAdapter = ScheduleAdapter(viewModel)
        binding.rcySchedule.adapter = scheduleAdapter

        val routeOwnerAdapter = RouteOwnerAdapter(viewModel)
        binding.rcyRoutePhoto.adapter = routeOwnerAdapter

        viewModel.selectSchedule.observe(viewLifecycleOwner, Observer {
            Logger.d("selectSchedule0000=$it")
            it?.let {
                viewModel.getRouteOwners(it.owners)
                (activity as MainActivity).endRoute.value = it
                binding.rcyFacility.visibility = View.GONE
                viewModel.clearMarker()
                showBottomSheet()
                if (it.list != listOf<NavInfo>()) {
                    binding.textNoRoute.visibility = View.GONE
                    binding.imageNoRoute.visibility =View.GONE
                    for (i in it.list) {
                        i.meter = i.latLng.getDinstance(UserManager.user.geo)
                        mapFragment.getMapAsync(viewModel.onlyRouteMark(i.latLng, i.title))
                        Logger.d("getEachDistance=${i.meter}")
                    }
                    val sortList = it.list.sortedBy { it.meter }
                    Logger.d("sortlist=$sortList")
                    (binding.rcySchedule.adapter as ScheduleAdapter).submitList(sortList)
                    (binding.rcySchedule.adapter as ScheduleAdapter).notifyDataSetChanged()
                }else{
                    (binding.rcySchedule.adapter as ScheduleAdapter).submitList(it.list)
                    binding.textNoRoute.visibility = View.VISIBLE
                    binding.imageNoRoute.visibility =View.VISIBLE
                }
            }

        })

        viewModel.routeOwners.observe(viewLifecycleOwner , Observer {
            Logger.d("routeOwners=$it")
            it?.let {
                (binding.rcyRoutePhoto.adapter as RouteOwnerAdapter).submitList(it)
                (binding.rcyRoutePhoto.adapter as RouteOwnerAdapter).notifyDataSetChanged()
            }
        })

        viewModel.deleteNavInfo.observe(viewLifecycleOwner, Observer {nav ->
            viewModel.selectSchedule.value?.let {schedule ->
                val list = schedule.list.toMutableList()
                Logger.d("listnav=$list")
                list.remove(nav)
                val route = Route(schedule.id, schedule.name, schedule.owners, schedule.open, list)
                viewModel.publishRoute(route)
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
            mapFragment.getMapAsync(viewModel.onlyRouteMark(it.latLng, it.title))
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
                Logger.d("friendsMarkers=${viewModel.friendMarkers}")
                viewModel.clearFriendMarker()
                it.forEach {user ->
                    mapFragment.getMapAsync(viewModel.onlyAddMarkFriend(user.geo, user.email, user.picture))
                }
            }
        })

        viewModel.needfocus.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("needfocus=$it")
            }
        })

        viewModel.selectFriend.observe(viewLifecycleOwner, Observer {user ->
            Logger.d("selectFriend=$user")
            val list = UserManager.friends.filter { it.email == user.email }
            Logger.d("filterfriends=$list")
            if (list != listOf<User>()) {
                mapFragment.getMapAsync(viewModel.onlyMoveCamera(list[0].geo, 18f))
                mapFragment.getMapAsync(viewModel.onlyAddMarkFriend(list[0].geo, list[0].email, list[0].picture))
                (activity as MainActivity).info.value =
                    NavInfo(title = list[0].email.getEmailName(), latLng = list[0].geo, imageUrl = list[0].picture)
                Control.hasPolyline = false
                binding.rcyFacility.visibility = View.GONE
            }
        })

        viewModel.clickRoute.observe(viewLifecycleOwner, Observer {
            it?.let{
                if (viewModel.selectSchedule.value?.list != listOf<NavInfo>()) {
                    viewModel.selectSchedule.value?.list?.get(0)?.latLng?.let { position ->
                        Handler().postDelayed(Runnable {
                            mapFragment.getMapAsync(viewModel.onlyMoveCamera(position, 16f))
                        }, 200L)
                    }
                }
            }
        })

        viewModel.liveRoutes.observe(viewLifecycleOwner, Observer {
            Logger.d("liveRoutes=$it")
            var list = mutableListOf<Route>()
            it?.forEach {
                val route = it.toRoute()
                list.add(route)
            }
            MockData.routes = list
            Logger.d("MockRoutes=${MockData.routes}")

            if (!viewModel.addNewRoute && !Control.addNewAnimal && bottomBehavior.state != BottomSheetBehavior.STATE_HIDDEN){
                viewModel.selectSchedule.value?.let {
                    val filter = MockData.routes.filter { route -> route.name == it.name }
                    viewModel.selectSchedule.value = filter[0]
                    Logger.d("fireSelectSchedule=${filter[0]}")
                }
            }
            viewModel.addNewRoute = false
            Control.addNewAnimal = false
        })

        viewModel.cooperateConfirm.observe(viewLifecycleOwner, Observer {
            Logger.d("cooperateConfirm=$it")
            it?.let {checks ->
                viewModel.selectSchedule.value?.let {route ->
                    val routeNow = route
                    val oriOwners = route.owners.toMutableList()
                    oriOwners.addAll(checks)
                    routeNow.owners = oriOwners
                    viewModel.publishRoute(routeNow)
                }
            }
        })
        viewModel.needMapIcon.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it){
                    binding.buttonMyLocation.setImageResource(R.drawable.icon_map)
                }else{
                    binding.buttonMyLocation.setImageResource(R.drawable.icon_mylocation2)
                }
            }
        })

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
            Logger.d("isBackMap=${viewModel.isBackMap}")
            val myDistance = UserManager.user.geo.getDinstance(viewModel.mapCenter)
            if (viewModel.isBackMap < 0){
                if ((activity as MainActivity).isLocationEnable()) {
//                        if (myDistance < 460 ){
                        mapFragment.getMapAsync(viewModel.myLocationCall)
                        viewModel.isBackMap = viewModel.isBackMap * (-1)
                        viewModel.needMapIcon.value = true
//                    }else{
//                        Toast.makeText(ZooApplication.appContext, "目前不在動物園範圍內", Toast.LENGTH_LONG).show()
//                    }
                } else
                    viewModel.toast(getString(R.string.cant_get_new_location))
            }else{
                mapFragment.getMapAsync(viewModel.callback1)
                viewModel.isBackMap = viewModel.isBackMap * (-1)
                viewModel.needMapIcon.value = false
            }
        }

        binding.switchMarkers.setOnToggledListener(this)

        binding.buttonRefresh.setOnClickListener {
            viewModel.selectSchedule.value = viewModel.selectSchedule.value
            mapFragment.getMapAsync(viewModel.selectSchedule.value?.list?.get(0)?.latLng?.let { location ->
                viewModel.onlyMoveCamera(location, 16f)
            })
            binding.rcyFacility.visibility = View.GONE
        }
        binding.buttonEarser.setOnClickListener {
            viewModel.clearPolyline()
            binding.rcyFacility.visibility = View.GONE
            mapFragment.getMapAsync(viewModel.callback1)
        }
        binding.imageFacBack.setOnClickListener {
            binding.rcyFacility.smoothScrollToPosition(0)
            mapFragment.getMapAsync(viewModel.callback1)
        }
        binding.imageCloseFac.setOnClickListener {
            if(viewModel.selectSchedule.value != null)
                viewModel.selectSchedule.value = viewModel.selectSchedule.value
            else
                viewModel.clearMarker()
            binding.rcyFacility.visibility = View.GONE
            mapFragment.getMapAsync(viewModel.callback1)
        }
        binding.buttonCooperate.setOnClickListener {
            viewModel.selectSchedule.value?.owners?.let { owners -> viewModel.showCoorperate(owners)}
        }
        binding.buttonBackFocus.setOnClickListener {
            mapFragment.getMapAsync(viewModel.onlyMoveCamera(UserManager.user.geo, 19f))
            viewModel.needfocus.value = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
         mapFragment.getMapAsync(activity as MainActivity)
         mapFragment.getMapAsync(viewModel.allMarks)
         mapFragment.getMapAsync(markerCall)
         mapFragment.getMapAsync(viewModel.checkCameraMove)
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
        (activity as MainActivity).info.value = null
        (activity as MainActivity).markInfo.value = null
        (activity as MainActivity).selectFacility.value = null
        (activity as MainActivity).selectRoute.value = null
    }

    val markerCall = OnMapReadyCallback { googleMap ->
        googleMap.setOnMarkerClickListener {
            Logger.d("marker=${it.title}")
            //diaable MapTool
            googleMap.uiSettings.isMapToolbarEnabled = false

            //rcyFacility move to position
            viewModel.newFacList.value?.let {list ->
                var position = -1
                for ((index,facility) in list.withIndex()){
                    if (facility.geo[0] == it.position){
                        position = index
                    }
                }
                if (position != -1)
                    binding.rcyFacility.smoothScrollToPosition(position)
            }

            //get clickMark and marker location
            viewModel.clickMark.value = it
            viewModel.navLatLng.value = it.position

            //get images
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
                image = filterFac[0].image
            }else if (filterFriend != listOf<User>()){
                imageUrl = filterFriend[0].picture
            }else
                image = 0

            val location = LatLng(it.position.latitude, it.position.longitude)
            (activity as MainActivity).info.value = NavInfo(it.title.getEmailName(), location, image = image, imageUrl = imageUrl)
            Control.hasPolyline = false
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
                .setFabBackgroundColor(resources.getColor(R.color.colorPrimary))
                .setLabel(getString(R.string.fab_clear))
                .setLabelColor(Color.BLACK)
                .setLabelBackgroundColor(resources.getColor(R.color.colorPrimary))
                .setLabelClickable(true)
                .create())
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_friend, R.drawable.icon_frined)
                .setFabBackgroundColor(resources.getColor(R.color.colorPrimary))
                .setLabel(getString(R.string.fab_friend))
                .setLabelColor(Color.BLACK)
                .setLabelBackgroundColor(resources.getColor(R.color.colorPrimary))
                .setLabelClickable(true)
                .create())
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_schedule, R.drawable.icon_cat)
                .setFabBackgroundColor(resources.getColor(R.color.colorPrimary))
                .setLabel(getString(R.string.fab_route))
                .setLabelColor(Color.BLACK)
                .setLabelBackgroundColor(resources.getColor(R.color.colorPrimary))
                .setLabelClickable(true)
                .create())

        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_clear -> {
                    Logger.d("sam_fab clear")
                    if (viewModel.selectSchedule.value != null)
                        viewModel.selectSchedule.value = null
                    (activity as MainActivity).endRoute.value = null
                    (activity as MainActivity).selectNavAnimal.value = null
                    viewModel.clearMarker()
                    viewModel.clearPolyline()
                    mapFragment.getMapAsync(viewModel.callback1)
                    binding.rcyFacility.visibility = View.GONE
                    hideBottomSheet()
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_friend -> {
                    if (UserManager.friends != listOf<User>()) {
                        Logger.d("sam_fab friend")
                        viewModel.needfocus.value = false
                        showFriends()
                        speedDialView.close()
                    }else{
                        (activity as MainActivity).toast(getString(R.string.no_friend_yet))
                    }
                    // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_schedule -> {
                    Logger.d("sam_fab schedule")
                    viewModel.showSelectAlert()
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
            }
            false
        })

        speedDialView.setOnChangeListener(object : SpeedDialView.OnChangeListener {
            override fun onMainActionSelected(): Boolean {
                Logger.d("onMainActionSelected")
                return false // True to keep the Speed Dial open
            }

            override fun onToggleChanged(isOpen: Boolean) {
                Logger.d("onToggleChanged")
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
        viewModel.visibleFriend = (viewModel.visibleFriend)*(-1)
        if (viewModel.visibleFriend > 0){
            UserManager.friends.forEach {
                mapFragment.getMapAsync(viewModel.onlyAddMarkFriend(it.geo, it.email, it.picture))
            }
            binding.rcyFriends.visibility = View.VISIBLE
            val sortMeter = UserManager.friends.sortedBy { it.geo.getDinstance(UserManager.user.geo) }
            mapFragment.getMapAsync(viewModel.onlyMoveCamera(sortMeter[0].geo, 16.5f))
        }else{
            viewModel.clearFriendMarker()
            binding.rcyFriends.visibility = View.GONE
        }
        Logger.d("visibleFriend=${viewModel.visibleFriend}")
    }

}
