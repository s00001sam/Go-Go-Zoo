package com.sam.gogozoo.schedulepage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sam.gogozoo.databinding.ScheduleFragmentBinding
import com.sam.gogozoo.ext.getVmFactory

class ScheduleFragment : Fragment() {

    private val viewModel by viewModels<ScheduleViewModel> { getVmFactory() }

    companion object {
        fun newInstance() = ScheduleFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ScheduleFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner


        return binding.root
    }


}
