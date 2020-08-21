package com.sam.gogozoo.ext

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.factory.ViewModelFactory

fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as ZooApplication).repository
    return ViewModelFactory(repository)
}

fun Activity?.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}