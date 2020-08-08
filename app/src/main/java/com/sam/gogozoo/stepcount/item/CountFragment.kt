package com.sam.gogozoo.stepcount.item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.MainViewModel
import com.sam.gogozoo.R
import com.sam.gogozoo.data.Control
import com.sam.gogozoo.data.StepInfo
import com.sam.gogozoo.databinding.FragmentCountBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Util.to2fString
import com.sam.gogozoo.util.Util.to3fString
import com.sam.gogozoo.util.Util.toTimeString
import com.sam.gogozoo.data.UserManager

class CountFragment : Fragment() {

    private val countZero = "0"

    private val viewModel by viewModels<CountViewModel> { getVmFactory() }
    lateinit var binding: FragmentCountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate the layout for this fragment
        binding = FragmentCountBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        binding.buttonRestart.setOnClickListener {
            setRestart()
        }
        binding.buttonStop.setOnClickListener {
            setStop()
        }

        mainViewModel.nowStepInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                changeText(it)
            }
        })
        mainViewModel.timeCount.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textTimeCount.text =
                    "${((it)/60).toTimeString()} : ${((it)% 60).toTimeString()}"
            }
        })

        return binding.root
    }

    private fun changeText(stepInfo: StepInfo) {
        binding.textKmNum.text = stepInfo.kilometer.to3fString()
        binding.textStepNum.text = stepInfo.step.toString()
        binding.textCalNum.text = stepInfo.kcal.to2fString()
    }

    private fun setStop() {
        (activity as MainActivity).stopService()
        viewModel.publishStep(UserManager.newStep)
        viewModel.setShowStop(false)
        Control.showStop = false
    }

    private fun setRestart() {
        binding.textTimeCount.text = getString(R.string.zero_time_count)
        binding.textKmNum.text = getString(R.string.zero_km)
        binding.textStepNum.text = countZero
        binding.textCalNum.text = "0.00"
        (activity as MainActivity).startService()
        viewModel.setShowStop(true)
        Control.showStop = true
    }

}
