package com.sam.gogozoo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.info.InfoViewModel
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.source.PublisherRepository

@Suppress("UNCHECKED_CAST")
class InfoViewModelFactory(
    private val repository: PublisherRepository,
    private val navInfo: NavInfo?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(InfoViewModel::class.java) ->
                    InfoViewModel(repository, navInfo)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}