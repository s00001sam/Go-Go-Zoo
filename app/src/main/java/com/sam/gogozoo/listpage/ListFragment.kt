package com.sam.gogozoo.listpage

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.databinding.FragmentListBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.toast

class ListFragment : Fragment() {

    private val viewModel by viewModels<ListViewModel> { getVmFactory() }
    private lateinit var binding: FragmentListBinding

    companion object {
        fun newInstance() = ListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.mockAreas.value = MockData.localAreas.sortedBy { it.meter }

        val adapter = ListPageAdapter(ListPageAdapter.OnclickListener{
            viewModel.displayArea(it)
        },viewModel)
        binding.rcyList.adapter = adapter

        viewModel.mockAreas.observe(viewLifecycleOwner, Observer {
            it?.let {
                (binding.rcyList.adapter as ListPageAdapter).submitList(it)
            }
        })

        viewModel.navigationArea.observe(viewLifecycleOwner, Observer {
            if (null != it){
                Logger.d("clickArea=$it")
                this.findNavController().navigate(ListFragmentDirections.actionListFragmentToDetailAreaFragment(it))
                viewModel.displayAreaComplete()
            }
        })

//        binding.mRreshLayout.setOnRefreshListener {
//            refresh()
//        }

        return binding.root
    }

//    fun refresh(){
//        toast("更新中", ZooApplication.appContext)
//        binding.mRreshLayout.isRefreshing = true
//        Handler().postDelayed({
//            binding.mRreshLayout.isRefreshing = false
//        },3000)
//
//    }
}
