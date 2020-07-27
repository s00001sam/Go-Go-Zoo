package com.sam.gogozoo.web

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sam.gogozoo.R
import com.sam.gogozoo.databinding.DialogWebBinding
import com.sam.gogozoo.ext.getVmFactory


class WebDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<WebDialogViewModel> { getVmFactory() }
    lateinit var binding: DialogWebBinding

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
        binding = DialogWebBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.webDialog.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_enter))

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("sam","leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        binding.buttonSure.setOnClickListener {
            val uri: Uri = Uri.parse("https://www.zoo.gov.taipei/")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }


        return binding.root
    }

    override fun dismiss() {
        binding.webDialog.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_out))
        Handler().postDelayed({ super.dismiss() }, 200)
    }

}
