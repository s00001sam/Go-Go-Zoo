package com.sam.gogozoo.facilityDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.*
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sam.gogozoo.MainViewModel
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.MockData
import com.sam.gogozoo.data.facility.LocalFacility
import com.sam.gogozoo.databinding.DialogFacilityBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger

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
        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        //get FacilityItem
        val listFac = FacilityDialogArgs.fromBundle(requireArguments()).facility
        viewModel.setListFac(listFac)

        val listView = binding.mListView

        viewModel.listFac.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter = FacilityAdapter(ZooApplication.appContext, android.R.layout.simple_list_item_1, it.item)
                listView.adapter = adapter
            }
        })

        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            viewModel.setSelectItem(adapter.getItem(position))
        }

        viewModel.selectItem.observe(viewLifecycleOwner, Observer {
            it?.let {
                setClickFacility(it, mainViewModel)
                dismiss()
            }
        })

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        return binding.root
    }

    private fun setClickFacility(string: String?, mainViewModel: MainViewModel) {
        var selectItem = listOf<LocalFacility>()
        val selectCategory =
            MockData.localFacility.filter { it.category == viewModel.listFac.value?.category }
        if (string != getString(R.string.text_all)) {
            selectItem = selectCategory.filter { it.item == string }.sortedBy { it.meter }
        } else {
            selectItem = selectCategory.sortedBy { it.meter }
        }
        mainViewModel.setSelectFacility(selectItem)
        Logger.d("selectFacility=${mainViewModel.selectFacility.value}")
    }


}
