package com.sam.gogozoo.facilityDialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.*
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.databinding.DialogFacilityBinding
import com.sam.gogozoo.ext.getVmFactory

class FacilityDialog : AppCompatDialogFragment() {

    companion object {
        fun newInstance() = FacilityDialog()
    }

    private val viewModel by viewModels<FacilityDialogViewModel> { getVmFactory(
        FacilityDialogArgs.fromBundle(requireArguments()).facility
    ) }

    lateinit var binding: DialogFacilityBinding

    lateinit var adapter: FacilityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            DialogFragment.STYLE_NO_FRAME,
            R.style.LoginDialog
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFacilityBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.listFac.value = FacilityDialogArgs.fromBundle(requireArguments()).facility

        val listView = binding.mListView

        viewModel.listFac.observe(viewLifecycleOwner, Observer {
            Log.d("sam", "listFac=$it")
            adapter = FacilityAdapter(ZooApplication.appContext, android.R.layout.simple_list_item_1, it.item)
            listView.adapter = adapter
        })

        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
                Log.d("sam", "item=${adapter.getItem(position)}")
                viewModel.selectItem.value = adapter.getItem(position)
            }

        viewModel.selectItem.observe(viewLifecycleOwner, Observer {string ->
            val selectCategory = MockData.localFacility.filter { it.category == viewModel.listFac.value?.category }
            val selectItem = selectCategory.filter { it.item == string }
            (activity as MainActivity).selectFacility.value = selectItem
            Log.d("sam", "selectFacility=${(activity as MainActivity).selectFacility.value}")
            dismiss()
        })


        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("sam","leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })



        return binding.root
    }



}
