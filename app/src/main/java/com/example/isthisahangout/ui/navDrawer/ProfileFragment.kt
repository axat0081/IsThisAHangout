package com.example.isthisahangout.ui.navDrawer

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.ComfortCharacterAdapter
import com.example.isthisahangout.databinding.FragmentProfileBinding
import com.example.isthisahangout.models.ComfortCharacter
import com.example.isthisahangout.models.FirebasePost
import com.example.isthisahangout.service.uploadService.FirebaseUploadService
import com.example.isthisahangout.viewmodel.FirebaseAuthViewModel
import com.example.isthisahangout.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile),
    ComfortCharacterAdapter.OnItemClickListener {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<FirebaseAuthViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()
    private lateinit var comfortCharacterAdapter: ComfortCharacterAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)
        comfortCharacterAdapter = ComfortCharacterAdapter(this)
        userViewModel.userId.value = viewModel.userId.value
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.username.collectLatest { name ->
                    usernameTextView.text = name
                }
            }
            pfpImageview.setOnClickListener {
                viewModel.imageTag.value = "pfp"
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragment2ToProfileImageUpdateFragment()
                )
            }
            headerImageView.setOnClickListener {
                viewModel.imageTag.value = "header"
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragment2ToProfileImageUpdateFragment()
                )
            }
            comfortCharacterTextRecyclerview.apply {
                adapter = comfortCharacterAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            addComfortCharacterButton.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragment2ToAddComfortCharacterFragment()
                )
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.userPfp.collectLatest { pfp ->
                    Glide.with(requireContext())
                        .load(pfp)
                        .into(pfpImageview)
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.userHeader.collectLatest { header ->
                    Glide.with(requireContext())
                        .load(header)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                imageProgressBar.isVisible = false
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                imageProgressBar.isVisible = false
                                return false
                            }
                        }).into(headerImageView)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.profileFlow.collect { event ->
                    when (event) {
                        is FirebaseAuthViewModel.AuthEvent.UpdateProfileSuccess -> {
                            Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT)
                                .show()
                            pfpProgressBar.isVisible = true
                            Glide.with(requireContext())
                                .load(viewModel.profilePfp)
                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        pfpProgressBar.isVisible = false
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        pfpProgressBar.isVisible = false
                                        return false
                                    }
                                }).into(pfpImageview)
                        }
                        is FirebaseAuthViewModel.AuthEvent.RegistrationFailure -> {
                            Snackbar.make(
                                requireView(),
                                "Error: ${event.message}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                userViewModel.comfortCharacters.collectLatest { comfortCharacters ->
                    comfortCharactersProgressBar.isVisible = false
                    comfortCharactersErrorTextView.isVisible = false
                    comfortCharacterAdapter.submitList(comfortCharacters)

                }
            }
            binding.postsByUserView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            binding.postsByUserView.setContent {
                val userPosts = userViewModel.userPosts.value
                val isLoading = userViewModel.isLoading.value
                val isEndReached = userViewModel.isEndOfUserPostPagination.value
                LazyColumn {
                    itemsIndexed(items = userPosts) { index, post ->
                        if (index >= userPosts.size - 1 && !isLoading && !isEndReached) {
                            userViewModel.getUserPosts()
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .clickable(
                                    enabled = true,
                                    onClick = {
                                        val clickedPost = userPosts[index]
                                        onPostClick(clickedPost)
                                    }
                                )
                        ) {
                            Text(
                                text = post.title ?: "",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                style = MaterialTheme.typography.h3,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = post.text ?: "",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(character: ComfortCharacter) {

    }

    private fun onPostClick(post: FirebasePost) {

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