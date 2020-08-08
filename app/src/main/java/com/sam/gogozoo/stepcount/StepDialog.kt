package com.sam.gogozoo.stepcount

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.sam.gogozoo.R
import com.sam.gogozoo.databinding.DialogStepBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.stepcount.item.CountFragment
import com.sam.gogozoo.stepcount.item.RecordFragment
import com.sam.gogozoo.util.Logger

class StepDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<StepViewModel> { getVmFactory() }
    lateinit var binding: DialogStepBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            DialogFragment.STYLE_NO_FRAME,
            R.style.LoginDialog
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate the layout for this fragment
        binding = DialogStepBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.stepDialog.startAnimation(AnimationUtils.loadAnimation(context, R.anim.step_dialog_enter))

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        setTabAndViewPager()

        return binding.root
    }

    private fun setTabAndViewPager() {
        val tabLayout: TabLayout = binding.tabsStep
        val viewPager: ViewPager = binding.viewpagerStep
        val viewPagerAdapter = StepAdapter(childFragmentManager)
        viewPagerAdapter.addFragment(CountFragment(), getString(R.string.title_count))
        viewPagerAdapter.addFragment(RecordFragment(), getString(R.string.title_record))
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

}
