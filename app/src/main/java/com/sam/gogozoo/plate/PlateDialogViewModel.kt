package com.sam.gogozoo.plate

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sam.gogozoo.R
import com.sam.gogozoo.data.source.ZooRepository

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

    fun toast(text: String, context: Context) {
        val toast = Toast(context)
        val view = LayoutInflater.from(context).inflate(R.layout.toast, null)
        val textView = view.findViewById<TextView>(R.id.toastText)
        textView.text = text
        toast.view = view
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.TOP, -220, 100)
        toast.show()
    }

}
