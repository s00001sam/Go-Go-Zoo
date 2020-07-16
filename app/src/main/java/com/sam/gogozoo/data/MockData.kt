package com.sam.gogozoo.data

import com.google.android.gms.maps.model.LatLng
import com.sam.gogozoo.R
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.data.facility.LocalFacility

object MockData {

    var isfirstTime = false

    val allMarkers = mutableListOf<NavInfo>()

    var localAreas = listOf<LocalArea>()

    var localAnimals = listOf<LocalAnimal>()

    var localFacility = listOf<LocalFacility>()

//    var myLocation = LatLng(24.998361, 121.581033)

    val mapTopItem = listOf<String>("諮詢售票","醫護急救","交通服務","設施服務","餐飲服務")

    val listMapTopItem = listOf(
        FacilityItem(mapTopItem[0], listOf<String>("ALL", "志工服務台", "夜間開放", "售票處", "無障礙設施", "遊客服務中心")),
        FacilityItem(mapTopItem[1], listOf<String>("ALL", "AED", "急救箱", "護理站", "緊急電話")),
        FacilityItem(mapTopItem[2], listOf<String>("ALL", "列車站", "車站", "其他")),
        FacilityItem(mapTopItem[3], listOf<String>("ALL", "wifi熱點","哺集乳室", "廁所", "場地設施", "涼亭",
                                                    "娃娃車/輪椅租用", "寄物櫃")),
        FacilityItem(mapTopItem[4], listOf<String>("ALL", "商店", "販賣機", "飲水機", "團餐服務", "餐飲"))
    )

    var listFotFacImage = listOf<String>()

    val listTestSchedule = listOf(
        NavInfo(title= "大冠鷲", latLng = LatLng(24.9953205, 121.5800741)),
        NavInfo(title= "大食蟻獸", latLng = LatLng(24.9967502, 121.5824110)),
        NavInfo(title= "新光特展館（大貓熊館）", latLng = LatLng(24.9968265, 121.5830956)),
        NavInfo(title= "羊駝", latLng = LatLng(24.9986011, 121.5827215))
    )
    val scheduleTest = Schedule("推薦行程1", listTestSchedule)

    val listTestSchedule2 = listOf(
        NavInfo(title= "白手長臂猿", latLng = LatLng(24.9945482, 121.5831550)),
        NavInfo(title= "國王企鵝", latLng = LatLng(24.9931338, 121.5907654)),
        NavInfo(title= "河馬", latLng = LatLng(24.9977223, 121.5879670))
    )
    val scheduleTest2 = Schedule("推薦行程2", listTestSchedule2)

    var schedules = listOf(scheduleTest , scheduleTest2)

    val areas = listOf(
        OriMarkInfo(LatLng(24.9985962, 121.5805931),"臺灣動物區", R.drawable.icon_taiwan),
        OriMarkInfo(LatLng(24.9989718, 121.5819383),"兒童動物區", R.drawable.icon_child),
        OriMarkInfo(LatLng(24.9950215, 121.5834188),"熱帶雨林區", R.drawable.icon_rainforest),
        OriMarkInfo(LatLng(24.9967402, 121.5807004),"昆蟲館", R.drawable.icon_butterfly),
        OriMarkInfo(LatLng(24.9952621, 121.5851489),"沙漠動物區", R.drawable.icon_desert),
        OriMarkInfo(LatLng(24.994184, 121.5853326),"澳洲動物區", R.drawable.icon_australia),
        OriMarkInfo(LatLng(24.9951333, 121.5880094),"非洲動物區", R.drawable.icon_african),
        OriMarkInfo(LatLng(24.9931447, 121.5896013),"溫帶動物區", R.drawable.icon_temperatezone),
        OriMarkInfo(LatLng(24.9957179, 121.5888946),"鳥園區", R.drawable.icon_bird),
        OriMarkInfo(LatLng(24.9978621, 121.5818524),"教育中心", R.drawable.icon_house),
        OriMarkInfo(LatLng(24.9929758, 121.5911959),"企鵝館", R.drawable.icon_jackass_penguin),
        OriMarkInfo(LatLng(24.9982291, 121.5828744),"無尾熊館", R.drawable.icon_koala),
        OriMarkInfo(LatLng(24.9940697, 121.5898494),"兩棲爬蟲動物館", R.drawable.icon_amphibian_reptile),
        OriMarkInfo(LatLng(24.9968265, 121.5830956),"新光特展館（大貓熊館）", R.drawable.icon_panda),
        OriMarkInfo(LatLng(24.9967279, 121.5828662),"熱帶雨林室內館（穿山甲館）", R.drawable.icon_pangolin),
        OriMarkInfo(LatLng(24.9946471, 121.5742216),"酷Cool節能屋", R.drawable.icon_house),
        OriMarkInfo(LatLng(24.9987238, 121.5826075),"生命驛站", R.drawable.icon_house)
    )

    val animals = listOf(
        OriMarkInfo(LatLng(24.9971109, 121.5831587),"大貓熊", R.drawable.icon_panda),
        OriMarkInfo(LatLng(24.9931338, 121.5907654),"國王企鵝", R.drawable.icon_king_penguin),
        OriMarkInfo(LatLng(24.9928761, 121.5910631),"黑腳企鵝", R.drawable.icon_jackass_penguin),
//        OriMarkInfo(LatLng(24.9952281, 121.5852535),"弓角羚羊", R.drawable.icon_addax),
//        OriMarkInfo(LatLng(24.9988648, 121.5820804),"家雞", R.drawable.icon_chicken),
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
//        OriMarkInfo(LatLng(24.9967402, 121.5807004),"木蘭青鳳蝶", R.drawable.icon_butterfly),
        OriMarkInfo(LatLng(24.9963666, 121.5830852),"黑冠松鼠猴", R.drawable.icon_monkey),
//        OriMarkInfo(LatLng(24.9940697, 121.5898494),"中國鱷蜥", R.drawable.icon_lizard),
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