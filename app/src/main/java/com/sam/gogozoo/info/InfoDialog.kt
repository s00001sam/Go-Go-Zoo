package com.sam.gogozoo.info

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.R
import com.sam.gogozoo.data.Control
import com.sam.gogozoo.databinding.FragmentInfoDialogBinding
import com.sam.gogozoo.ext.getVmFactory

/**
 * A simple [Fragment] subclass.
 */
class InfoDialog : AppCompatDialogFragment() {

    lateinit var binding:FragmentInfoDialogBinding

    private val viewModel by viewModels<InfoViewModel> { getVmFactory(
        InfoDialogArgs.fromBundle(
            requireArguments()
        ).info) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME,
            R.style.LoginDialog
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoDialogBinding.inflate(inflater, container, false)
        binding.infoDialog.startAnimation(AnimationUtils.loadAnimation(context,
            R.anim.anim_slide_up
        ))
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val info =
            InfoDialogArgs.fromBundle(requireArguments()).info

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("sam","leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        viewModel.info.value = info

        binding.markTitle.text = info?.title
        binding.imageIcon.setImageResource(info?.image ?: R.drawable.icon_person)
        binding.buttonancel.setOnClickListener {
            dismiss()
        }
        binding.buttonNav.setOnClickListener {
            (activity as MainActivity).needNavigation.value = true
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun dismiss() {
        binding.infoDialog.startAnimation(AnimationUtils.loadAnimation(context,
            R.anim.anim_slide_down
        ))
        Handler().postDelayed({ super.dismiss() }, 200)
    }


}
