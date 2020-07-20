package com.sam.gogozoo.plate

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.sam.gogozoo.MainActivity
import com.sam.gogozoo.R
import com.sam.gogozoo.ZooApplication
import com.sam.gogozoo.data.User
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
        val codeFile = bitmap.toFile(activity as MainActivity)
        Logger.d("bitmap.toFile=$codeFile")

        binding.imageQR.setImageBitmap(bitmap)

        binding.buttonShare.setOnClickListener {
            shareImage()
        }

        //Open camera for scan
        binding.buttonCamera.setOnClickListener {
            val scanner = IntentIntegrator(activity as MainActivity)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()
        }

        binding.buttonEnter.setOnClickListener {
            val enter = viewModel.email.value
            val filter = UserManager.friends.filter{user -> user.email == enter}

            if (enter == UserManager.user.email){
                Toast.makeText(context, "不能和自己成為同伴", Toast.LENGTH_SHORT).show()
            }else if(filter != listOf<User>()){
                Toast.makeText(context, "${enter} 早已成為同伴", Toast.LENGTH_SHORT).show()
            }else{
                (activity as MainActivity).getFriend(enter ?: "")
            }
        }


        return binding.root
    }


    fun shareImage(){
        //share image
        try {

//            val imagePath = File((activity as MainActivity).getFilesDir(), "images")
            val newFile = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), ZOOQR)
            val contentUri =
                FileProvider.getUriForFile(requireContext(), "com.sam.gogozoo.fileprovider", newFile)

            Logger.d("contentUri=$contentUri")
//            requireContext().grantUriPermission("com.sam.gogozoo.plate", contentUri,
//                Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, "Send QR Code via")
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
