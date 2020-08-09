package com.sam.gogozoo.plate

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.MainViewModel
import com.sam.gogozoo.R
import com.sam.gogozoo.data.User
import com.sam.gogozoo.data.UserManager
import com.sam.gogozoo.data.source.ZooRepository
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util
import com.sam.gogozoo.util.Util.getString
import com.sam.gogozoo.util.Util.toFile

class PlateDialogViewModel(private val repository: ZooRepository) : ViewModel() {

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave
    
    val email = MutableLiveData<String>()

    init {
    }

    fun leave() {
        _leave.value = true
    }

    fun onLeaveCompleted() {
        _leave.value = null
    }

    fun nothing() {}

    fun addFriend(mainViewModel: MainViewModel, context: Context?) {
        val enter = email.value
        val filter = UserManager.friends.filter { user -> user.email == enter }
        if (enter == UserManager.user.email) {
            Util.toast(getString(R.string.text_cant_add_yourself), context)
        } else if (filter != listOf<User>()) {
            Util.toast("${enter} 早已成為同伴", context)
        } else {
            mainViewModel.checkUser(enter ?: "")
        }
    }

    fun setScanner(activity: MainActivity){
        val scanner = IntentIntegrator(activity)
        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        scanner.setBeepEnabled(false)
        scanner.initiateScan()
    }

}
