package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentProfileImageUpdateBinding
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.example.isthisahangout.viewmodel.FirebaseAuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileImageUpdateFragment : DialogFragment(R.layout.fragment_profile_image_update) {
    private var _binding: FragmentProfileImageUpdateBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<FirebaseAuthViewModel>()
    private lateinit var cropImage: ActivityResultLauncher<CropImageContractOptions>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileImageUpdateBinding.bind(view)
        cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                val uri = result.uriContent
                if (viewModel.imageTag.value == "pfp")
                    viewModel.profilePfp = uri
                else
                    viewModel.profileHeader = uri
            } else {
                val error = result.error
                error?.let { exception ->
                    Snackbar.make(
                        requireView(),
                        exception.localizedMessage!!.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                if (viewModel.imageTag.value == "pfp") {
                    viewModel.userPfp.collectLatest { image ->
                        Glide.with(requireContext())
                            .load(image)
                            .into(imageview)
                    }
                } else {
                    viewModel.userHeader.collectLatest { image ->
                        Glide.with(requireContext())
                            .load(image)
                            .into(imageview)
                    }
                }
            }
            selectImageButton.setOnClickListener {
                if (viewModel.imageTag.value == "pfp") {
                    cropImage.launch(
                        options {
                            setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(70, 70)
                                .setCropShape(CropImageView.CropShape.OVAL)
                        }
                    )
                } else {
                    cropImage.launch(
                        options {
                            setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1920, 1080)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                        }
                    )
                }
            }
            uploadButton.setOnClickListener {
                Snackbar.make(
                    requireView(),
                    "Updating profile",
                    Snackbar.LENGTH_SHORT
                ).show()
                if (viewModel.imageTag.value == "pfp")
                    viewModel.onUpdatePfpClick()
                else
                    viewModel.onUpdateHeaderClick()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(viewModel.broadcastReceiver, FirebaseUploadService.intentFilter)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(viewModel.broadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}