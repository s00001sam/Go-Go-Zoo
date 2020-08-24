package com.sam.gogozoo.plate

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.MainViewModel
import com.sam.gogozoo.R
import com.sam.gogozoo.data.UserManager
import com.sam.gogozoo.databinding.DialogPlateBinding
import com.sam.gogozoo.ext.getVmFactory
import com.sam.gogozoo.util.Logger
import com.sam.gogozoo.util.Util.toFile
import java.io.File

class PlateDialog : AppCompatDialogFragment() {

    private val ZOOQR = "gogozooMyQR.png"

    private val viewModel by viewModels<PlateDialogViewModel> { getVmFactory() }
    lateinit var binding: DialogPlateBinding

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
        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        binding.plateDialog.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_enter))

        viewModel.leave.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("leave=$it")
                dismiss()
                viewModel.onLeaveCompleted()
            }
        })

        //generate QR Code
        generateQRCode()

        binding.buttonShare.setOnClickListener {
            shareImage()
        }

        //Open camera for scan
        binding.buttonCamera.setOnClickListener {
            viewModel.setScanner(activity as MainActivity)
        }

        binding.buttonEnter.setOnClickListener {
            viewModel.addFriend(mainViewModel, context)
        }

        return binding.root
    }

    private fun generateQRCode() {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap =
            barcodeEncoder.encodeBitmap(UserManager.user.email, BarcodeFormat.QR_CODE, 300, 300)
        val codeFile = bitmap.toFile(activity as MainActivity)
        Logger.d("bitmap.toFile=$codeFile")
        binding.imageQR.setImageBitmap(bitmap)
    }

    override fun dismiss() {
        binding.plateDialog.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_out))
        Handler().postDelayed({ super.dismiss() }, 200)
    }


    fun shareImage(){
        //share image
        try {
            val newFile = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), ZOOQR)
            val contentUri =
                FileProvider.getUriForFile(requireContext(), "com.sam.gogozoo.fileprovider", newFile)

            Logger.d("contentUri=$contentUri")

            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, getString(R.string.send_qr_code))
                putExtra(Intent.EXTRA_STREAM, contentUri)
                setType("image/*")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }, null)
            startActivity(share)
        } catch (e: Exception) {
            Logger.d("Exception111")
            e.printStackTrace()
        }
    }

}
