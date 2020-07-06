package com.sam.gogozoo.ext

import androidx.fragment.app.Fragment
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.factory.InfoViewModelFactory
import com.sam.gogozoo.factory.ViewModelFactory


/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Extension functions for Fragment.
 */
fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as ZooApplication).repository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(navInfo: NavInfo?): InfoViewModelFactory {
    val repository = (requireContext().applicationContext as ZooApplication).repository
    return InfoViewModelFactory(repository, navInfo)
}

//fun Fragment.getVmFactory(author: Author?): AuthorViewModelFactory {
//    val repository = (requireContext().applicationContext as PublisherApplication).repository
//    return AuthorViewModelFactory(repository, author)
//}