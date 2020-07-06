package com.sam.gogozoo.homepage

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Looper
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.OriMarkInfo
import com.sam.gogozoo.data.source.PublisherRepository
import com.sam.gogozoo.data.model.DirectionResponses
import com.sam.gogozoo.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val repository: PublisherRepository) : ViewModel() {

    //create some variables for address
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()
    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // status for the loading icon of swl
    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    val myLatLng = MutableLiveData<LatLng>()

    val info = MutableLiveData<NavInfo>()
    val navLatLng = MutableLiveData<LatLng>()

    var hasPoly = false

    val polyList = mutableListOf<Polyline>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)



    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Call getArticlesResult() on init so we can display status immediately.
     */
    init {

    }
    val callback1 = OnMapReadyCallback { it ->
        val x = 0.0045
        val y = 0.004
        val cameraPosition =
            CameraPosition.builder().target(LatLng(24.998361-y, 121.581033+x)).zoom(16f).bearing(146f)
                .build()
        it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }


    fun directionCall(location1: LatLng, location2: LatLng, key: String) = OnMapReadyCallback { map ->

        val myPosition =
            CameraPosition.builder().target(location1).zoom(20f).bearing(146f).tilt(45f).build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition))

        val fromFKIP = location1.latitude.toString() + "," + location1.longitude.toString()
        val toMonas = location2.latitude.toString() + "," + location2.longitude.toString()

//        val apiServices = RetrofitClient.apiServices(this)
        repository.getDirection(fromFKIP, toMonas, key)
            .enqueue(object : Callback<DirectionResponses> {
                override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                    Log.d("bisa dong oke", "sam1234 ${response.message()}")
                    drawPolyline(map,response)
                }
                override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                    Log.e("anjir error", "sam1234 ${t.localizedMessage}")
                }
            })

        hasPoly = true
    }

    fun drawPolyline(map: GoogleMap, response: Response<DirectionResponses>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        val polylineOption = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(Color.RED)
        val polyline = map.addPolyline(polylineOption)
        polyList.add(polyline)
    }

    fun clearPolyline(){
        polyList.forEach {
            it.remove()
        }
    }

    //get location LatLng
    //allow us to get the last location
    fun getNewLocation(){
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper()
        )
    }

    //create the location callback
    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
            //set the new location
            Log.d("sam", "sam1234${lastLocation.latitude}, ${lastLocation.longitude}")
            myLatLng.value = LatLng(lastLocation.latitude, lastLocation.longitude)
        }
    }

    val animalMarks = OnMapReadyCallback { googleMap ->
        animals.map {animal ->
            googleMap.addMarker(MarkerOptions().position(animal.latLng).title(animal.title).icon(
                changeBitmapDescriptor(animal.drawable)))
        }
        googleMap.setOnMarkerClickListener {
            Log.d("sam","marker=${it.title}")
            false
        }
    }

    fun changeBitmapDescriptor(drawable: Int):BitmapDescriptor {
        val bitmapdraw = getDrawable(ZooApplication.appContext, drawable) as BitmapDrawable
        val b: Bitmap = bitmapdraw.bitmap
        val smallMarker: Bitmap = Bitmap.createScaledBitmap(b, 40, 40, false)
        return BitmapDescriptorFactory.fromBitmap(smallMarker)
    }

    val animals = listOf(
        OriMarkInfo(LatLng(24.9971109, 121.5831587),"大貓熊", R.drawable.icon_panda),
        OriMarkInfo(LatLng(24.9931338, 121.5907654),"國王企鵝", R.drawable.icon_king_penguin),
        OriMarkInfo(LatLng(24.9928761, 121.5910631),"黑腳企鵝", R.drawable.icon_jackass_penguin),
        OriMarkInfo(LatLng(24.9952281, 121.5852535),"弓角羚羊", R.drawable.icon_addax),
        OriMarkInfo(LatLng(24.9988648, 121.5820804),"家雞", R.drawable.icon_chicken),
        OriMarkInfo(LatLng(24.9951066, 121.5856424),"非洲野驢", R.drawable.icon_wildass),
        OriMarkInfo(LatLng(24.9952026, 121.5856987),"單峰駱駝", R.drawable.icon_camelus_dromedarius),
        OriMarkInfo(LatLng(24.9949741, 121.5855928),"雙峰駱駝", R.drawable.icon_twohumped_came),
        OriMarkInfo(LatLng(24.9952326, 121.5834381),"人猿", R.drawable.icon_orangutan),
        OriMarkInfo(LatLng(24.9955632, 121.5833201),"大長臂猿", R.drawable.icon_siamang),
        OriMarkInfo(LatLng(24.9944558, 121.5831872),"印度大犀鳥", R.drawable.icon_bicornis),
        OriMarkInfo(LatLng(24.9941690, 121.5829350),"亞洲象", R.drawable.icon_elephant),
        OriMarkInfo(LatLng(24.9941782, 121.5832372),"孟加拉虎", R.drawable.icon_bengal_tiger),
        OriMarkInfo(LatLng(24.9944267, 121.5829350),"花豹", R.drawable.icon_leopard),
        OriMarkInfo(LatLng(24.9988648, 121.5820804),"家鵝", R.drawable.icon_goose),
        OriMarkInfo(LatLng(24.9943343, 121.5828868),"馬來熊", R.drawable.icon_malayan_sun_bear),
        OriMarkInfo(LatLng(24.9963569, 121.5827151),"馬來貘", R.drawable.icon_tapir),
        OriMarkInfo(LatLng(24.9940478, 121.5840505),"黑天鵝", R.drawable.icon_black_swan),
        OriMarkInfo(LatLng(24.9945482, 121.5831550),"白手長臂猿", R.drawable.icon_white_handed_gibbon),
        OriMarkInfo(LatLng(24.9988648, 121.5820804),"家鴨", R.drawable.icon_duck),
        OriMarkInfo(LatLng(24.9986011, 121.5827215),"羊駝", R.drawable.icon_alpaca),
        OriMarkInfo(LatLng(24.9992732, 121.5823621),"長鼻浣熊", R.drawable.icon_coati),
        OriMarkInfo(LatLng(24.9995479, 121.5823366),"家驢", R.drawable.icon_donkey),
        OriMarkInfo(LatLng(24.9995540, 121.5824398),"迷你馬", R.drawable.icon_horse),
        OriMarkInfo(LatLng(24.9967113, 121.5825773),"黑蜘蛛猴", R.drawable.icon_monkey),
        OriMarkInfo(LatLng(24.9992805, 121.5824023),"絨鼠", R.drawable.icon_mouse),
        OriMarkInfo(LatLng(24.9992805, 121.5824023),"狐獴", R.drawable.icon_meerkat),
        OriMarkInfo(LatLng(24.9967402, 121.5807004),"木蘭青鳳蝶", R.drawable.icon_butterfly),
        OriMarkInfo(LatLng(24.9963666, 121.5830852),"黑冠松鼠猴", R.drawable.icon_monkey),
        OriMarkInfo(LatLng(24.9940697, 121.5898494),"中國鱷蜥", R.drawable.icon_lizard),
        OriMarkInfo(LatLng(24.9960747, 121.5833432),"紅腿陸龜", R.drawable.icon_turtle),
        OriMarkInfo(LatLng(24.9946641, 121.5873295),"蘇卡達象龜", R.drawable.icon_turtle),
        OriMarkInfo(LatLng(24.9938388, 121.5878686),"北非髯羊", R.drawable.icon_barbary_sheep),
        OriMarkInfo(LatLng(24.9942472, 121.5888302),"白頸狐猴", R.drawable.icon_monkey),
        OriMarkInfo(LatLng(24.9948999, 121.5869164),"伊蘭羚", R.drawable.icon_eland),
        OriMarkInfo(LatLng(24.9940418, 121.5884265),"東非狒狒", R.drawable.icon_monkey),
        OriMarkInfo(LatLng(24.9938291, 121.5874207),"金剛猩猩", R.drawable.icon_gorilla),
        OriMarkInfo(LatLng(24.9952111, 121.5884534),"非洲象", R.drawable.icon_elephant),
        OriMarkInfo(LatLng(24.9946252, 121.5870304),"非洲獅", R.drawable.icon_lion),
        OriMarkInfo(LatLng(24.9952463, 121.5865181),"查普曼斑馬", R.drawable.icon_zebra),
        OriMarkInfo(LatLng(24.9944271, 121.5887457),"格利威斑馬", R.drawable.icon_zebra),
        OriMarkInfo(LatLng(24.9940357, 121.5871498),"斑哥條紋羚", R.drawable.icon_eland),
        OriMarkInfo(LatLng(24.9935373, 121.5872853),"黑猩猩", R.drawable.icon_chimpanzee),
        OriMarkInfo(LatLng(24.9942508, 121.5889603),"聖環", R.drawable.icon_lbis),
        OriMarkInfo(LatLng(24.9944234, 121.5863049),"網紋長頸鹿", R.drawable.icon_giraffe),
        OriMarkInfo(LatLng(24.9941657, 121.5881959),"鴕鳥", R.drawable.icon_ostrich),
        OriMarkInfo(LatLng(24.9951075, 121.5878731),"侏儒河馬", R.drawable.icon_pigmy_hippopotamus),
        OriMarkInfo(LatLng(24.9977223, 121.5879670),"河馬", R.drawable.icon_hippo),
        OriMarkInfo(LatLng(24.9957508, 121.5901324),"青鸞", R.drawable.icon_great_argus),
        OriMarkInfo(LatLng(24.9954408, 121.5898159),"白頰椋鳥", R.drawable.icon_pied_mynah),
        OriMarkInfo(LatLng(24.9953205, 121.5901834),"綠絲冠僧帽鳥", R.drawable.icon_turaco),
        OriMarkInfo(LatLng(24.9951126, 121.5894029),"禿鸛", R.drawable.icon_stork),
        OriMarkInfo(LatLng(24.9953205, 121.5800741),"大冠鷲", R.drawable.icon_serpent_eagle),
        OriMarkInfo(LatLng(24.9954153, 121.5902263),"大巨嘴鳥", R.drawable.icon_toco_toucan),
        OriMarkInfo(LatLng(24.9961029, 121.5827949),"維多利亞冠鴿", R.drawable.icon_victoria),
        OriMarkInfo(LatLng(24.9981684, 121.5805918),"梅花鹿", R.drawable.icon_sika_deer),
        OriMarkInfo(LatLng(24.99528898, 121.5912335),"白鳳頭鸚鵡", R.drawable.icon_white_cockatoo),
        OriMarkInfo(LatLng(24.9953083, 121.5909746),"小紅鶴", R.drawable.icon_lesser_flamingo),
        OriMarkInfo(LatLng(24.9951831, 121.5911798),"白枕鶴", R.drawable.icon_white_naped_crane),
        OriMarkInfo(LatLng(24.9953144, 121.5909398),"智利紅鶴", R.drawable.icon_chilean_flamingo),
        OriMarkInfo(LatLng(24.992842, 121.5900023),"歐亞大山貓", R.drawable.icon_eurasian_lynx),
        OriMarkInfo(LatLng(24.9983738, 121.5823688),"無尾熊", R.drawable.icon_koala),
        OriMarkInfo(LatLng(24.9925005, 121.5905508),"小貓熊", R.drawable.icon_lesser_panda),
        OriMarkInfo(LatLng(24.9924968, 121.5894082),"加拿大河狸", R.drawable.icon_canadian_beaver),
        OriMarkInfo(LatLng(24.9932772, 121.5900815),"北美灰狼", R.drawable.icon_gray_wolf),
        OriMarkInfo(LatLng(24.9927679, 121.5906286),"阿拉斯加棕熊", R.drawable.icon_brown_bear),
        OriMarkInfo(LatLng(24.9931994, 121.5902773),"紅耳龜", R.drawable.icon_red_turtle),
        OriMarkInfo(LatLng(24.9921553, 121.5890408),"黑尾草原犬鼠", R.drawable.icon_prairie_dog),
        OriMarkInfo(LatLng(24.9971255, 121.5809003),"石虎", R.drawable.icon_leopard_cat),
        OriMarkInfo(LatLng(24.9976518, 121.580109),"白鼻心", R.drawable.icon_civet),
        OriMarkInfo(LatLng(24.9973151, 121.5810397),"穿山甲", R.drawable.icon_pangolin),
        OriMarkInfo(LatLng(24.9983057, 121.5802887),"臺灣野豬", R.drawable.icon_taiwanpig),
        OriMarkInfo(LatLng(24.9975801, 121.5799735),"臺灣黑熊", R.drawable.icon_taiwan_bear),
        OriMarkInfo(LatLng(24.9973102, 121.580691),"臺灣獼猴", R.drawable.icon_taiwan_monkey),
        OriMarkInfo(LatLng(24.9979496, 121.5799159),"領角鴞", R.drawable.icon_scops_owl),
        OriMarkInfo(LatLng(24.9973589, 121.5810706),"鼬獾", R.drawable.icon_subaurantiaca),
        OriMarkInfo(LatLng(24.9946848, 121.5858972),"灰袋鼠", R.drawable.icon_giganteus),
        OriMarkInfo(LatLng(24.9947079, 121.5851864),"南方食火雞", R.drawable.icon_southern_cassowary),
        OriMarkInfo(LatLng(24.9952354, 121.590866),"丹頂鶴", R.drawable.icon_tancho),
        OriMarkInfo(LatLng(24.9967356, 121.5827115),"水豚", R.drawable.icon_capybara),
        OriMarkInfo(LatLng(24.9967356, 121.5827383),"黑掌蜘蛛猴", R.drawable.icon_spider_monkey),
        OriMarkInfo(LatLng(24.9967502, 121.5824110),"大食蟻獸", R.drawable.icon_giant_anteater),
        OriMarkInfo(LatLng(24.9964779, 121.5826846),"南美小食蟻獸", R.drawable.icon_tamandua),
        OriMarkInfo(LatLng(24.9964244, 121.5829153),"白面捲尾猴", R.drawable.icon_capuchin),
        OriMarkInfo(LatLng(24.9957681, 121.5832211),"紅藍吸蜜鸚鵡", R.drawable.icon_redandblue_lory),
        OriMarkInfo(LatLng(24.9959431, 121.5833445),"阿氏夜猴", R.drawable.icon_night_monkey),
        OriMarkInfo(LatLng(24.9960452, 121.5833176),"二趾樹獺", R.drawable.icon_two_toed_sloth),
        OriMarkInfo(LatLng(24.9960014, 121.5832050),"慈鯛", R.drawable.icon_cichlid),
        OriMarkInfo(LatLng(24.9958556, 121.5832640),"棉頭絹猴", R.drawable.icon_cottontop_tamarin),
        OriMarkInfo(LatLng(24.9958021, 121.5832747),"金色箭毒蛙", R.drawable.icon_poison_frog)
    )

}
