package com.sam.gogozoo.stepcount.item

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import com.sam.gogozoo.R
import com.sam.gogozoo.data.StepInfo
import com.sam.gogozoo.databinding.FragmentRecordBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger

class RecordFragment : Fragment() {

    private val viewModel by viewModels<RecordViewModel> { getVmFactory() }
    lateinit var binding: FragmentRecordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate the layout for this fragment
        binding = FragmentRecordBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = RecordAdapter(viewModel)
        binding.rcyRecord.adapter = adapter

        viewModel.liveSteps.observe(viewLifecycleOwner, Observer {
            Logger.d("liveSteps???=$it")
            it?.let {
                viewModel.hasData.value = it != listOf<StepInfo>()
                binding.viewModel = viewModel
                (binding.rcyRecord.adapter as RecordAdapter).submitList(it)
            }
        })


        return binding.root
    }


}
