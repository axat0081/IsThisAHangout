package com.example.isthisahangout.ui.navDrawer

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentAddComfortCharacterBinding
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.example.isthisahangout.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddComfortCharacterFragment : DialogFragment(R.layout.fragment_add_comfort_character) {
    private var _binding: FragmentAddComfortCharacterBinding? = null
    private val binding get() = _binding!!
    private lateinit var cropImage: ActivityResultLauncher<CropImageContractOptions>
    private val viewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddComfortCharacterBinding.bind(view)
        cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                val uri = result.uriContent
                viewModel.comfortCharacterPic = uri
                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.characterImageView)
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
            nameEditText.editText!!.addTextChangedListener { name ->
                viewModel.comfortCharacterName = name.toString()
            }
            fromEditText.editText!!.addTextChangedListener { from ->
                viewModel.comfortCharacterFrom = from.toString()
            }
            descEditText.editText!!.addTextChangedListener { desc ->
                viewModel.comfortCharacterDesc = desc.toString()
            }
            addImageButton.setOnClickListener {
                cropImage.launch(
                    options {
                        setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1920, 1080)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                    }
                )
            }
            uploadButton.setOnClickListener {
                viewModel.onAddComfortCharacterClick()
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.databaseEventFlow.collectLatest { event ->
                    when (event) {
                        is UserViewModel.DatabaseEvent.DatabaseSuccess -> {
                            Snackbar.make(
                                requireView(),
                                event.msg,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is UserViewModel.DatabaseEvent.DatabaseFailure -> {
                            Snackbar.make(
                                requireView(),
                                event.msg,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
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