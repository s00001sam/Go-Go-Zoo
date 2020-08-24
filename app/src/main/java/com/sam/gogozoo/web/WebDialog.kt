package com.sam.gogozoo.web

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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
import com.sam.gogozoo.util.Logger


class WebDialog : AppCompatDialogFragment() {

    private val ZOOURI = "https://www.zoo.gov.taipei/"
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
                Logger.d("leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        binding.buttonSure.setOnClickListener {
            goZooWeb()
        }
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }


        return binding.root
    }

    private fun goZooWeb() {
        val uri: Uri = Uri.parse(ZOOURI)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun dismiss() {
        binding.webDialog.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_out))
        Handler().postDelayed({ super.dismiss() }, 200)
    }

}
