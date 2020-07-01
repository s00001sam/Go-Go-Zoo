package com.sam.gogozoo.listpage

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sam.gogozoo.databinding.ListFragmentBinding
import com.sam.gogozoo.ext.getVmFactory

class ListFragment : Fragment() {

    private val viewModel by viewModels<ListViewModel> { getVmFactory() }

    companion object {
        fun newInstance() = ListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ListFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner


        return binding.root
    }


}
