package com.example.isthisahangout.ui.createContent

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentUploadVideoBinding
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.example.isthisahangout.viewmodel.VideoViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UploadVideoFragment : Fragment(R.layout.fragment_upload_video) {
    private var _binding: FragmentUploadVideoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<VideoViewModel>()
    private lateinit var cropImage: ActivityResultLauncher<CropImageContractOptions>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUploadVideoBinding.bind(view)
        binding.apply {
            val getVideoUri =
                registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    if (uri != null) {
                        viewModel.videoUrl = uri
                    }
                }
            cropImage = registerForActivityResult(CropImageContract()) { result ->
                if (result.isSuccessful) {
                    val uri = result.uriContent
                    viewModel.videoThumbnail = uri
                    Glide.with(requireContext())
                        .load(uri)
                        .into(binding.cropImageView)
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
            videoTitleEditText.editText!!.addTextChangedListener { title ->
                viewModel.videoTitle = title.toString()
            }
            videoTextEditText.editText!!.addTextChangedListener { text ->
                viewModel.videoText = text.toString()
            }
            selectVideoButton.setOnClickListener {
                getVideoUri.launch("video/*")
            }
            selectThumbnailButton.setOnClickListener {
                cropImage.launch(
                    options {
                        setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1920, 1080)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                    }
                )
            }
            uploadSongButton.setOnClickListener {
                hideKeyboard(requireContext())
                viewModel.onUploadClick()
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.videoEventFlow.collect { event ->
                    when (event) {
                        is VideoViewModel.VideoEvent.UploadVideoSuccess -> {
                            Snackbar.make(
                                requireView(),
                                event.message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is VideoViewModel.VideoEvent.UploadVideoError -> {
                            Snackbar.make(
                                requireView(),
                                event.message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun hideKeyboard(mContext: Context) {
        val imm = mContext
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            requireActivity().window
                .currentFocus!!.windowToken, 0
        )
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
