package com.sam.gogozoo.introductions

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.sam.gogozoo.R
import com.sam.gogozoo.databinding.DialogIntroSearchBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger

class IntroSearchDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<IntroSearchDialogViewModel> { getVmFactory() }
    lateinit var binding: DialogIntroSearchBinding

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
        binding = DialogIntroSearchBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("leave=$it")
                when (it){
                    0 ->
                        binding.textntro.text = getString(R.string.welcome_introduction)
                    1 ->
                        binding.textntro.text = getString(R.string.search_introduction)
                    2 ->
                        binding.textntro.text = getString(R.string.fac_introduction)
                    3 ->
                        binding.textntro.text = getString(R.string.open_introduction)
                    4 ->
                        binding.textntro.text = getString(R.string.step_introduction)
                    5 ->
                        binding.textntro.text = getString(R.string.float_introduction)
                    6 ->
                        binding.textntro.text = getString(R.string.location_introduction)
                    7 ->
                        binding.textntro.text = getString(R.string.welcome_again__introduction)
                    else ->
                        dismiss()
                }
            }
        })


        return binding.root
    }


}
