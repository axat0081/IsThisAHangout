package com.example.isthisahangout.ui.createContent

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentCreatePostBinding
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.example.isthisahangout.viewmodel.PostViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePostFragment : Fragment(R.layout.fragment_create_post) {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PostViewModel>()
    private lateinit var cropImage: ActivityResultLauncher<CropImageContractOptions>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreatePostBinding.bind(view)
        cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                val uri = result.uriContent
                viewModel.postImage = uri
                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.croppedPostImageView)
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
            postTitleEditText.editText!!.addTextChangedListener { title ->
                viewModel.postTitle = title.toString()
            }
            postTextEditText.editText!!.addTextChangedListener { text ->
                viewModel.postText = text.toString()
            }
            uploadImageButton.setOnClickListener {
                cropImage.launch(
                    options {
                        setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1920, 1080)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                    }
                )
            }
            createPostsButton.setOnClickListener {
                hideKeyboard(requireContext())
                viewModel.onCreatePostClick()
                findNavController().navigate(
                    CreatePostFragmentDirections.actionCreatePostFragmentToPostsFragment2()
                )
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.postsEventFlow.collect { event ->
                    when (event) {
                        is PostViewModel.PostsEvent.CreatePostSuccess -> {
                            Snackbar.make(
                                requireView(),
                                event.message,
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }
                        is PostViewModel.PostsEvent.CreatePostError -> {
                            Snackbar.make(
                                requireView(),
                                event.message,
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
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