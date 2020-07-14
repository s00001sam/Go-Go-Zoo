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
import com.sam.gogozoo.bindImageCircle
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.databinding.DialogInfoBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.homepage.HomeFragmentDirections
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.getFragmentNavController

/**
 * A simple [Fragment] subclass.
 */
class InfoDialog : AppCompatDialogFragment() {

    lateinit var binding:DialogInfoBinding
    var filterArea = listOf<LocalArea>()
    var filterAnimal = listOf<LocalAnimal>()

    private val viewModel by viewModels<InfoViewModel> { getVmFactory(
        InfoDialogArgs.fromBundle(requireArguments()).info)
    }

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
        binding = DialogInfoBinding.inflate(inflater, container, false)
        binding.infoDialog.startAnimation(AnimationUtils.loadAnimation(context,
            R.anim.anim_slide_up
        ))
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val info = InfoDialogArgs.fromBundle(requireArguments()).info

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("sam","leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        viewModel.info.value = info

        info?.let {
            binding.markTitle.text = info.title
            Logger.d("samtitle=${it.title}")

            if (it.image != 0)
                binding.imageIcon.setImageResource(info.image)
            else
                bindImageCircle(binding.imageIcon, it.imageUrl)

            filterArea = MockData.localAreas.filter { area -> area.name == it.title }
            filterAnimal = MockData.localAnimals.filter { animal -> animal.nameCh == it.title }
            Logger.d("samtitlearea=$filterArea")
            Logger.d("samtitleanimal=$filterAnimal")
        }

        showInfoButton()

        binding.buttonInfo.setOnClickListener {
            dismiss()
            if (filterAnimal != listOf<LocalAnimal>()){
                Logger.d("navigation")
                    findNavController().navigate(InfoDialogDirections.actionGlobalDetailAnimalFragment(filterAnimal[0]))
            }else{
                    findNavController().navigate(InfoDialogDirections.actionGlobalDetailAreaFragment(filterArea[0]))
            }
        }

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

    fun showInfoButton(){
        if (filterArea == listOf<LocalArea>() && filterAnimal == listOf<LocalAnimal>()){
            binding.buttonInfo.visibility = View.GONE
        }else{
            binding.buttonInfo.visibility = View.VISIBLE
        }
    }


}
