package com.sam.gogozoo.info

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sam.gogozoo.*
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.NavInfo
import com.sam.gogozoo.data.User
import com.sam.gogozoo.data.UserManager
import com.sam.gogozoo.data.animal.LocalAnimal
import com.sam.gogozoo.data.area.LocalArea
import com.sam.gogozoo.databinding.DialogInfoBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.getEmailName

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
        viewModel.setContext(context)
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
        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val info = InfoDialogArgs.fromBundle(requireArguments()).info
        viewModel.setInfo(info)

        info?.let {
            viewModel.checkFriend(it)
            setTitleAndImage(it)
            filterArea = MockData.localAreas.filter { area -> area.name == it.title }
            filterAnimal = MockData.localAnimals.filter { animal -> animal.nameCh == it.title }
        }

        showInfoButton()

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        viewModel.selectSchedule.observe(viewLifecycleOwner, Observer {
            it?.let {
                mainViewModel.setSelectRoute(it)
            }
        })


        binding.buttonInfo.setOnClickListener {
            dismiss()
            navigateOption()
        }

        binding.buttonRoute.setOnClickListener {
            viewModel.showRouteName(viewModel.listRoute.toTypedArray())
        }

        binding.buttonancel.setOnClickListener {
            dismiss()
        }

        binding.buttonNav.setOnClickListener {
            mainViewModel.setNeedNavigation(true)
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun navigateOption() {
        if (filterAnimal != listOf<LocalAnimal>()) {
            Logger.d("navigation")
            findNavController().navigate(
                InfoDialogDirections.actionGlobalDetailAnimalFragment(
                    filterAnimal[0]
                )
            )
        } else {
            findNavController().navigate(
                InfoDialogDirections.actionGlobalDetailAreaFragment(
                    filterArea[0]
                )
            )
        }
    }

    private fun setTitleAndImage(it: NavInfo) {
        binding.markTitle.text = it.title
        when {
            it.image != 0 -> {
                binding.imageIcon.setImageResource(it.image)
            }
            it.imageUrl != "" -> {
                bindImageCircle(binding.imageIcon, it.imageUrl)
            }
            else -> {
                binding.imageIcon.setImageResource(R.drawable.icon_house)
            }
        }
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
