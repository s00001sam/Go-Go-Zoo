package com.sam.gogozoo.homepage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.github.angads25.toggle.interfaces.OnToggledListener
import com.github.angads25.toggle.model.ToggleableView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.MainViewModel
import com.sam.gogozoo.R
import com.sam.gogozoo.data.*
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.databinding.HomeFragmentBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.getDinstance
import kotlinx.android.synthetic.main.home_fragment.*
import com.sam.gogozoo.util.Util.getEmailName
import com.sam.gogozoo.util.Util.toast


class HomeFragment : Fragment(), OnToggledListener{

    private val viewModel by viewModels<HomeViewModel> { getVmFactory(
        HomeFragmentArgs.fromBundle(requireArguments()).route
    ) }

    lateinit var binding: HomeFragmentBinding
    lateinit var mapFragment: SupportMapFragment
    //bottomSheet
    lateinit var bottomBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setContext(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val selectRoute = HomeFragmentArgs.fromBundle(requireArguments()).route
        selectRoute?.let {
            viewModel.setSelectRoute(it)
        }

        initSpeedbutton()
        viewModel.getNewLocation()
        //show info dialog
        mainViewModel.info.observe(viewLifecycleOwner, Observer {
            Logger.d("navInfo=$it")
            it?.let{
                viewModel.clickMark.value?.hideInfoWindow()
                viewModel.setNeedFocus(false)
                Handler().postDelayed(Runnable {
                    findNavController().navigate(HomeFragmentDirections.actionGlobalInfoDialog(it))
                }, 500L)
            }
        })

        mainViewModel.markInfo.observe(viewLifecycleOwner, Observer {
            viewModel.clearMarker()
            it?.let {
                viewModel.setNeedFocus(false)
                binding.rcyFacility.visibility = View.GONE
                mapFragment.getMapAsync(viewModel.markCallback1(it.latLng, it.title))
            }
        })

        //direction to selected marker
        mainViewModel.needNavigation.observe(viewLifecycleOwner, Observer {
            it?.let {
                getMyDirection()
            }
        })

        mainViewModel.selectRoute.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.setSelectRoute(it)
                (binding.rcySchedule.adapter as ScheduleAdapter).notifyDataSetChanged()
            }
        })

        val facAdapter = HomeFacAdapter(viewModel)
        binding.rcyFacility.adapter = facAdapter

        mainViewModel.selectFacility.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("selectFacility=$it")
                selectFacility(it)
            }
        })

        mainViewModel.selectNavAnimal.observe(viewLifecycleOwner, Observer {
            it?.let { selectNavAnimals(it) }
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
            position?.let {
                if (mainViewModel.selectFacility.value != listOf<LocalFacility>()) {
                    mainViewModel.selectFacility.value?.let {
                        mapFragment.getMapAsync(viewModel.onlyMoveCamera(it[position].geo[0], 19f))
                    }
                }
            }
        })

        viewModel.selectFac.observe(viewLifecycleOwner, Observer {
            it?.let {
                setSelectFacInfo(it)
            }
        })

        viewModel.myLatLng.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.getAllDistance(it)
                viewModel.getPhotoToNet()
                cameraFollow(it)
                refreshRcyRoute()
                arrive(it)
                viewModel.needFriendLocation()
            }
        })

        //set up top recycleView
        binding.rcyHomeTop.adapter = HomeTopAdapter(viewModel)
        (binding.rcyHomeTop.adapter as HomeTopAdapter).submitList(MockData.mapTopItem)

        viewModel.selectTopItem.observe(viewLifecycleOwner, Observer {string ->
            string?.let {
                navigationFacilityDialog(it)
            }
        })

        val scheduleAdapter = ScheduleAdapter(viewModel)
        binding.rcySchedule.adapter = scheduleAdapter

        val routeOwnerAdapter = RouteOwnerAdapter(viewModel)
        binding.rcyRoutePhoto.adapter = routeOwnerAdapter

        viewModel.selectRoute.observe(viewLifecycleOwner, Observer {
            it?.let {
                showSelectRoute(it)
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
            nav?.let {
                viewModel.deleteNavFromRoute(it)
            }
        })

        viewModel.edit.observe(viewLifecycleOwner, Observer {
            it?.let {
                setViewEditRoute(it)
            }
        })

        viewModel.selectRoutePosition.observe(viewLifecycleOwner, Observer {
            it?.let {
                mapFragment.getMapAsync(viewModel.onlyRouteMark(it.latLng, it.title))
                mapFragment.getMapAsync(viewModel.onlyMoveCamera(it.latLng, 19f))
                mainViewModel.setInfo(it)
                Control.hasPolyline = false
                collapseBottomSheet()
            }
        })

        viewModel.user.observe(viewLifecycleOwner, Observer {
            viewModel.publishFriend(UserManager.user.email, it)
        })

        val friendAdapter = FriendAdapter(viewModel)
        binding.rcyFriends.adapter = friendAdapter

        viewModel.liveFriend.observe(viewLifecycleOwner, Observer {
            it?.let {
                UserManager.friends = it
                (binding.rcyFriends.adapter as FriendAdapter).submitList(it)
            }
        })

        viewModel.friendLocation.observe(viewLifecycleOwner, Observer {
            it?.let {
                changeFriendLocation(it)
            }
        })

        viewModel.selectFriend.observe(viewLifecycleOwner, Observer {
            it?.let {
                showClickFrient(it)
            }
        })

        viewModel.clickRoute.observe(viewLifecycleOwner, Observer {
            it?.let{
                viewModel.selectRouteAndMove(mapFragment)
            }
        })

        viewModel.liveRoutes.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.updateRoutes(it)
                updateUsingRoute()
            }
        })

        viewModel.cooperateConfirm.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.confirmCooperator(it)
            }
        })

        viewModel.needMapIcon.observe(viewLifecycleOwner, Observer {
            it?.let {
                changeMyLocationBtn(it)
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
            cameraToLocation()
        }

        binding.switchMarkers.setOnToggledListener(this)

        binding.buttonRefresh.setOnClickListener {
            viewModel.selectRoute.value?.let { viewModel.setSelectRoute(it) }
            mapFragment.getMapAsync(viewModel.selectRoute.value?.list?.get(0)?.latLng?.let { location ->
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
            closeRcyFacility()
        }
        binding.buttonCooperate.setOnClickListener {
            viewModel.selectRoute.value?.owners?.let { owners -> viewModel.showCoorperate(owners)}
        }
        binding.buttonBackFocus.setOnClickListener {
            mapFragment.getMapAsync(viewModel.onlyMoveCamera(UserManager.user.geo, 19f))
            viewModel.setNeedFocus(true)
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
        setBottomSheet()
        hideBottomSheet()
    }

    private fun setBottomSheet() {
        bottomBehavior = BottomSheetBehavior.from(bottom_dialog)
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
        mainViewModel.setInfo(null)
        mainViewModel.setMarkInfo(null)
        mainViewModel.setSelectFacility(null)
        mainViewModel.setSelectRoute(null)
    }

    val markerCall = OnMapReadyCallback { googleMap ->
        googleMap.setOnMarkerClickListener {
            Logger.d("marker=${it.title}")
            //disable MapTool
            googleMap.uiSettings.isMapToolbarEnabled = false
            //rcyFacility move to position
            scrollFacMoveCamera(it)
            //get clickMark and marker location
            viewModel.setClickMarker(it)
            //get navInfo
            mainViewModel.setInfo(viewModel.getMarkerNavInfo(it))
            Control.hasPolyline = false

            false
        }
    }

    private fun scrollFacMoveCamera(marker: Marker) {
        viewModel.newFacList.value?.let { list ->
            var position = -1
            for ((index, facility) in list.withIndex()) {
                if (facility.geo[0] == marker.position) {
                    position = index
                }
            }
            if (position != -1)
                binding.rcyFacility.smoothScrollToPosition(position)
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
            SpeedDialActionItem.Builder(R.id.fab_schedule, R.drawable.icon_cat)
                .setFabBackgroundColor(resources.getColor(R.color.colorPrimary))
                .setLabel(getString(R.string.fab_route))
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
            SpeedDialActionItem.Builder(R.id.fab_add_friend, R.drawable.icon_add_friend)
                .setFabBackgroundColor(resources.getColor(R.color.colorPrimary))
                .setLabel(getString(R.string.fab_add_friend))
                .setLabelColor(Color.BLACK)
                .setLabelBackgroundColor(resources.getColor(R.color.colorPrimary))
                .setLabelClickable(true)
                .create())

        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_clear -> {
                    Logger.d("sam_fab clear")
                    if (viewModel.selectRoute.value != null)
                            viewModel.setSelectRoute(null)
                    mainViewModel.setEndRoute(null)
                    mainViewModel.setSelectNavAnimal(null)
                    viewModel.clearMarker()
                    viewModel.clearPolyline()
                    viewModel.setNeedFocus(false)
                    mapFragment.getMapAsync(viewModel.callback1)
                    binding.rcyFacility.visibility = View.GONE
                    hideBottomSheet()
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_friend -> {
                    if (UserManager.friends != listOf<User>()) {
                        Logger.d("sam_fab friend")
                        viewModel.setNeedFocus(false)
                        showFriends()
                        speedDialView.close()
                    }else{
                        toast(getString(R.string.no_friend_yet), context)
                    }
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_schedule -> {
                    Logger.d("sam_fab schedule")
                    viewModel.showSelectAlert()
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_add_friend -> {
                    findNavController().navigate(R.id.plateDialog)
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


    //switch of animals' markers
    override fun onSwitched(toggleableView: ToggleableView?, isOn: Boolean) {
        if(isOn) {
            mapFragment.getMapAsync(viewModel.allMarks)
        } else {
            Logger.d("clearOk")
            viewModel.clearOriMarkers()
        }
    }

    fun showFriends(){
        viewModel.visibleFriend = (viewModel.visibleFriend) * (-1)
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

    fun getMyDirection(){
        val myDistance = UserManager.user.geo.getDinstance(viewModel.mapCenter)
//            if (myDistance < 460){
        viewModel.clearPolyline()
        val location1 = viewModel.myLatLng.value
        val location2 = mainViewModel.info.value?.latLng
        viewModel.setDirectionAim(location2)
        Logger.d("hasPoly=${Control.hasPolyline}")
        if (!Control.hasPolyline) {
            location1?.let {
                binding.rcyFacility.visibility = View.GONE
                mapFragment.getMapAsync(viewModel.directionCall(it, location2 ?: it))
                mapFragment.getMapAsync(viewModel.onlyMoveCamera(it, 18f))
            }
        }
//            }else{
//                toast("目前不在動物園範圍內")
//            }
    }

    fun selectFacility(facilities: List<LocalFacility>){
        viewModel.setNeedFocus(false)
        facilities.forEach {facility ->
            facility.meter = facility.geo[0].getDinstance(UserManager.user.geo)
            viewModel.clearMarker()
            mapFragment.getMapAsync(viewModel.onlyAddMark(facility.geo[0], facility.name))
            mapFragment.getMapAsync(viewModel.callback1)
        }
        val list = facilities.sortedBy { it.meter }
        viewModel.setNewFacList(list)
        (binding.rcyFacility.adapter as HomeFacAdapter).submitList(list)
        Control.hasPolyline = false
        viewModel.clickMark.value?.hideInfoWindow()
        Handler().postDelayed(Runnable {
            binding.rcyFacility.visibility = View.VISIBLE
        }, 200L)
    }

    fun selectNavAnimals(animals: List<LocalFacility>){
        viewModel.setNeedFocus(false)
        animals.forEach {facility ->
            facility.meter = facility.geo[0].getDinstance(UserManager.user.geo)
            viewModel.clearMarker()
            mapFragment.getMapAsync(viewModel.onlyAddMark(facility.geo[0], facility.name))
        }
        val list = animals.sortedBy { it.meter }
        mapFragment.getMapAsync(viewModel.onlyMoveCamera(list[0].geo[0], 16f))
        Control.hasPolyline = false
        viewModel.clickMark.value?.hideInfoWindow()
    }

    fun setSelectFacInfo(facility: LocalFacility){
        mapFragment.getMapAsync(viewModel.onlyMoveCamera(facility.geo[0], 18f))
        val isAnimal = MockData.localAnimals.filter {animal -> animal.nameCh == facility.name }
        if (isAnimal == listOf<LocalAnimal>()) {
            mainViewModel.setInfo(NavInfo(facility.name, facility.geo[0], image = R.drawable.icon_house))
        }else if (facility.imageUrl == ""){
            mainViewModel.setInfo(NavInfo(facility.name, facility.geo[0], image = R.drawable.image_placeholder))
        }else{
            mainViewModel.setInfo(NavInfo(facility.name, facility.geo[0], imageUrl = facility.imageUrl))
        }
    }

    fun arrive(latLng: LatLng){
        viewModel.directionAim.value?.let {aim ->
            if (viewModel.showRouteInfo.value == true && latLng.getDinstance(aim) < 20){
                toast(getString(R.string.text_arrive), context)
                viewModel.clearPolyline()
                Handler().postDelayed(Runnable {
                    mapFragment.getMapAsync(viewModel.callback1) }, 200L)
            }
        }
    }

    fun cameraFollow(latLng: LatLng){
        if (viewModel.needfocus.value == true){
            mapFragment.getMapAsync(viewModel.onlyMoveCamera(latLng, 19f))
        }
    }

    fun refreshRcyRoute(){
        if (bottomBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            viewModel.selectRoute.value?.list?.let { list ->
                list.forEach {
                    it.meter = it.latLng.getDinstance(UserManager.user.geo)
                }
                val newSortList = list.sortedBy { it.meter }
                (binding.rcySchedule.adapter as ScheduleAdapter).submitList(newSortList)
            }
        }
    }

    fun navigationFacilityDialog(string: String){
        val list = MockData.listMapTopItem.filter { it.category == string }
        val selectFacility = list[0]
        this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFacilityDialog(selectFacility))
    }

    fun showSelectRoute(route: Route){
        viewModel.getRouteOwners(route.owners)
        mainViewModel.setEndRoute(route)
        binding.rcyFacility.visibility = View.GONE
        viewModel.clearMarker()
        showBottomSheet()
        setRouteRecycleView(route)
    }

    private fun setRouteRecycleView(route: Route) {
        if (route.list != listOf<NavInfo>()) {
            binding.textNoRoute.visibility = View.GONE
            binding.imageNoRoute.visibility = View.GONE
            for (i in route.list) {
                i.meter = i.latLng.getDinstance(UserManager.user.geo)
                mapFragment.getMapAsync(viewModel.onlyRouteMark(i.latLng, i.title))
                Logger.d("getEachDistance=${i.meter}")
            }
            val sortList = route.list.sortedBy { it.meter }
            Logger.d("sortlist=$sortList")
            (binding.rcySchedule.adapter as ScheduleAdapter).submitList(sortList)
            (binding.rcySchedule.adapter as ScheduleAdapter).notifyDataSetChanged()
        } else {
            (binding.rcySchedule.adapter as ScheduleAdapter).submitList(route.list)
            binding.textNoRoute.visibility = View.VISIBLE
            binding.imageNoRoute.visibility = View.VISIBLE
        }
    }

    fun setViewEditRoute(boolean: Boolean){
        if (boolean == true) {
            binding.buttonEdit.visibility = View.GONE
            binding.buttonConfirm.visibility = View.VISIBLE
        }
        if (boolean == false) {
            binding.buttonEdit.visibility = View.VISIBLE
            binding.buttonConfirm.visibility = View.GONE
        }
        (binding.rcySchedule.adapter as ScheduleAdapter).notifyDataSetChanged()
    }

    fun changeFriendLocation(users: List<User>){
        UserManager.friends = users
        if (viewModel.visibleFriend > 0){
            viewModel.clearFriendMarker()
            users.forEach {user ->
                mapFragment.getMapAsync(viewModel.onlyAddMarkFriend(user.geo, user.email, user.picture))
            }
        }
    }

    fun showClickFrient(user: User){
        val list = UserManager.friends.filter { it.email == user.email }
        if (list != listOf<User>()) {
            mapFragment.getMapAsync(viewModel.onlyMoveCamera(list[0].geo, 18f))
            mapFragment.getMapAsync(viewModel.onlyAddMarkFriend(list[0].geo, list[0].email, list[0].picture))
            mainViewModel.setInfo(NavInfo(title = list[0].email.getEmailName(), latLng = list[0].geo, imageUrl = list[0].picture))
            Control.hasPolyline = false
            binding.rcyFacility.visibility = View.GONE
        }
    }

    fun updateUsingRoute(){
        if (!viewModel.addNewRoute && !Control.addNewAnimal && bottomBehavior.state != BottomSheetBehavior.STATE_HIDDEN){
            viewModel.selectRoute.value?.let {
                val filter = MockData.routes.filter { route -> route.name == it.name }
                viewModel.setSelectRoute(filter[0])
            }
        }
    }

    fun changeMyLocationBtn(boolean: Boolean){
        if (boolean){
            binding.buttonMyLocation.setImageResource(R.drawable.icon_map)
        }else{
            binding.buttonMyLocation.setImageResource(R.drawable.icon_mylocation2)
        }
    }

    fun cameraToLocation(){
        val myDistance = UserManager.user.geo.getDinstance(viewModel.mapCenter)
        if (viewModel.isBackMap < 0){
            if ((activity as MainActivity).isLocationEnable()) {
//                        if (myDistance < 460 ){
                mapFragment.getMapAsync(viewModel.myLocationCall)
                viewModel.isBackMap = viewModel.isBackMap * (-1)
                viewModel.setNeedMapIcon(true)
//                    }else{
//                        toast(getString(R.string.not_in_zoo))
//                    }
            } else
                toast(getString(R.string.cant_get_new_location), context)
        }else{
            mapFragment.getMapAsync(viewModel.callback1)
            viewModel.isBackMap = viewModel.isBackMap * (-1)
            viewModel.setNeedMapIcon(false)
        }
    }

    private fun closeRcyFacility() {
        if (viewModel.selectRoute.value != null)
            viewModel.setSelectRoute(viewModel.selectRoute.value)
        else
            viewModel.clearMarker()
        binding.rcyFacility.visibility = View.GONE
        mapFragment.getMapAsync(viewModel.callback1)
    }

}
