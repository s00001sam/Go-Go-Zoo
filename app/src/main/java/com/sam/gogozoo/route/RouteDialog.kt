package com.sam.gogozoo.route

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.sam.gogozoo.R
import com.sam.gogozoo.databinding.DialogRouteBinding
import com.sam.gogozoo.ext.getVmFactory

class RouteDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<RouteViewModel> { getVmFactory(
        RouteDialogArgs.fromBundle(requireArguments()).route
    )}
    lateinit var binding: DialogRouteBinding

    companion object {
        fun newInstance() = RouteDialog()
    }

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
        binding = DialogRouteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("sam","leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        val route = RouteDialogArgs.fromBundle(requireArguments()).route

        var adapter = route?.list?.let { RouteAdapter(it, viewModel) }
        val callback = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.rcyRoute)

        viewModel.selectRoute.value = route

        viewModel.selectRoute.observe(viewLifecycleOwner, Observer {
            adapter = RouteAdapter(it.list, viewModel)
            binding.rcyRoute.adapter = adapter
            (binding.rcyRoute.adapter as RouteAdapter).notifyDataSetChanged()
        })


        return binding.root
    }


}
