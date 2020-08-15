# gogoZoo
一款輕鬆暢遊台北動物園的 App  
<a href="https://play.google.com/store/apps/details?id=com.sam.gogozoo"><img src="https://i.imgur.com/B61ZlUR.png" width="110" height="50"></a>

<img src="https://i.imgur.com/7HpXpfA.png" width="120" height="240"/>

* [開發動機](#1)
* [功能特色](#2)
* [實作技術](#3)
* [開發環境](#4)
* [版本更新](#5)

<h3 id="1">開發動機</h3>

台北動物園是大人小孩都喜歡去的休閒場所，  
園區內的動物上百個，除了明星動物有自己的館區以外，  
其他想看的動物找尋起來都有一定的難度，  
這款 App 的初衷就是能夠幫助所有遊客，  
迅速導航到當下想看的動物，再也不用帶著遺憾離開園區

<h3 id="2">功能特色</h3>

<img src="https://i.imgur.com/LmRs1Gk.jpg" width="120" height="240"/><img src="https://i.imgur.com/n0HntJ9.png" width="120" height="240"/><img src="https://i.imgur.com/UhGIdsE.png" width="120" height="240"/><img src="https://i.imgur.com/hpNYTjk.png" width="120" height="240"/><img src="https://i.imgur.com/8b9XI6R.png" width="120" height="240"/><img src="https://i.imgur.com/e70zKgc.png" width="120" height="240"/>

【主畫面】  
主畫面為台北動物園地圖，並直接標示出各場區及動物位置, 提供您快速了解附近的動物及動物園分佈，上方的設施快捷按鍵區可方便您盡快找到所需的設備，不論是廁所，車站，還是商店都一覽無遺，並透過距離排序方式，帶領您前往最近的設施設備

【搜尋系統】  
主頁面右上角搜尋按鈕，提供您快速找到想看的動物

【導航系統】  
無論是動物，場區，設施，甚至是同伴，都可以經由一鍵導航的方式，帶領您迅速前往，清楚明瞭的路線規劃，並協助計算前往目的地之所需時間，讓您可以準確預估時間

【同伴系統】  
一趟快樂的旅途，總是免不了好友相伴，透過 QR Code 和同行好友們成為同伴，即使中途走失又或者想看不同動物，仍然可以隨時知道彼此位置，再也不需要出遊還要一直打電話或傳訊息詢問位置

【路線系統】  
一趟快樂的旅途，當然也少不了完整的規劃，透過路線規劃, 可以將想看的動物，想去的館區都安排妥當，也可以邀請同伴共同編輯，完成一趟大家都滿意的旅途，沿途中您的路線將隨著地點的遠近進行排序，讓您直覺的知道現在該前往哪裡

【動物清單】  
從這裡，可以清楚學習到有關動物們的科別種類，生活習性, 飲食等相關知識，讓您在遊園路上，也能不停增進自己動物方面的知識，實現寓教於樂的學習態度

【行事曆】  
方便您知道今天園區的所有活動，也可以一鍵導航前往該活動地點，若您想看非當天的活動，日期也都是可以做選擇的

【計步器】  
當程式啟動，會自動在背景幫使用者紀錄走路步數，提供使用者一個遊園的健康數據，也可以與過往的自己做個比較

<h3 id="3">實作技術</h3>

* 使用 MVVM 架構
* Navigation 完成所有頁面挑轉，透過 Safe Args 傳遞頁面資料
* LiveData 及 Observer 設計模式
* Google Map SDK 顯示台北動物園地圖及地圖基本操作
* Direction API 畫出導航路線
* Geocoder 取得目前座標位置
* Firestore 取的同伴目前位置
* QR Code 新增同伴，並使用 Content Provider 存取照片，Share Intent 分享照片
* BottomSheet 表示路線內容，並透過 Firestore 實現共同編輯
* RecyclerView 與 Snaphelper 完成滑動設施清單移動地圖聚焦位置
* SensorManager, Foreground Service 和 Notification 完成背景計步器
* Firebase Authentication 完成第三方登入
* Image Picker 及 Firebase Storage 完成個人照片更換及上傳
* 動物詳細照片使用無限 RecyclerView

<h3 id="4">開發環境</h3>

* android studio 3.6.3
* android SDK 29
* Gradle 5.6.4

<h3 id="5">版本更新</h3>

* 1.0.0 這是一款讓使用者輕鬆暢遊台北動物園的應用程式  
* 1.1.0 新增計步器功能與基本介面介紹  
* 1.1.1 修改計步器通知可滑動取消 與 修正動物資料頁面無照片閃退問題  
* 1.1.2 修正搜尋功能  
* 1.1.3 新增同伴按鈕及修正介面圖標  
