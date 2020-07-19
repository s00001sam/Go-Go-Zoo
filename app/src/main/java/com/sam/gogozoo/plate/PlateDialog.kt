package com.sam.gogozoo.plate

import android.content.Intent
import android.net.MailTo
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.R
import com.sam.gogozoo.data.UserManager
import com.sam.gogozoo.databinding.DialogPlateBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.toFile

class PlateDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<PlateDialogViewModel> { getVmFactory() }
    lateinit var binding: DialogPlateBinding

    companion object {
        fun newInstance() = PlateDialog()
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
        //Inflate the layout for this fragment
        binding = DialogPlateBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("sam","leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        //generate QR Code
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(UserManager.user.email, BarcodeFormat.QR_CODE, 300, 300)
        val codeUri = bitmap.toFile()
        Logger.d("bitmap.toFile=$codeUri")

        binding.imageQR.setImageBitmap(bitmap)

        binding.buttonShare.setOnClickListener {
            shareImage(codeUri)
        }

        //Open camera for scan
        binding.buttonCamera.setOnClickListener {
            val scanner = IntentIntegrator(activity as MainActivity)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()
        }


        return binding.root
    }


    fun shareImage(uri: Uri){
        //share image
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "image/*"

            val chooseIntent = Intent.createChooser(intent, "Share image via")
            chooseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity((context as MainActivity),chooseIntent, null)
        } catch (e: Exception) {
            Logger.d("Exception111")
            e.printStackTrace()
        }
    }

}
