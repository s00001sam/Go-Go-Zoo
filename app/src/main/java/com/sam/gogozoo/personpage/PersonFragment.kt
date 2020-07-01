package com.sam.gogozoo.personpage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sam.gogozoo.databinding.PersonFragmentBinding
import com.sam.gogozoo.ext.getVmFactory

class PersonFragment : Fragment() {

    private val viewModel by viewModels<PersonViewModel> { getVmFactory() }

    companion object {
        fun newInstance() = PersonFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = PersonFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner


        return binding.root
    }

}
