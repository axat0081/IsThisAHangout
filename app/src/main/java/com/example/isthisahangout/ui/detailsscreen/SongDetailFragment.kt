package com.example.isthisahangout.ui.detailsscreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.CommentsAdapter
import com.example.isthisahangout.databinding.FragmentSongDetailBinding
import com.example.isthisahangout.models.Comments
import com.example.isthisahangout.ui.components.MusicPlayerButtons
import com.example.isthisahangout.utils.LocalSpacing
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.utils.convertToProgress
import com.example.isthisahangout.viewmodel.detailScreen.SongDetailViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.util.*


@AndroidEntryPoint
class SongDetailFragment : Fragment(R.layout.fragment_song_detail),
    CommentsAdapter.OnItemLongClickListener, CommentsAdapter.OnReplyingToClickListener {
    private var _binding: FragmentSongDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<SongDetailFragmentArgs>()
    private lateinit var cropImage: ActivityResultLauncher<CropImageContractOptions>
    private val viewModel by viewModels<SongDetailViewModel>()
    private lateinit var commentsAdapter: CommentsAdapter

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSongDetailBinding.bind(view)
        val song = args.song
        commentsAdapter = CommentsAdapter(this)
        cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                val uri = result.uriContent
                viewModel.commentImage = uri
                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.addCommentImageView)
            } else {
                val error = result.error
                error?.let { exception ->
                    Toast.makeText(
                        requireContext(),
                        exception.localizedMessage!!.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
        binding.apply {
            commentRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = commentsAdapter
                itemAnimator = null
                isVisible = true
            }
            addCommentImageView.visibility = View.GONE

            songPlayerView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            songPlayerView.setContent {
                val musicState = viewModel.musicState.collectAsState()
                val currentPosition = viewModel.currentSongPosition.collectAsState()
                val songProgress by animateFloatAsState(
                    targetValue = convertToProgress(
                        count = currentPosition.value,
                        total = musicState.value.duration
                    )
                )
                val spacing = LocalSpacing.current
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsTopHeight(WindowInsets.statusBars)
                    )
                    Box(modifier = Modifier.padding(all = spacing.spaceExtraLarge)) {
                        AsyncImage(
                            model = ImageRequest.Builder(requireContext())
                                .data(song.thumbnail)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
                                .aspectRatio(1f)
                                .clip(MaterialTheme.shapes.medium)
                        )
                    }
                    Slider(
                        modifier = Modifier.padding(horizontal = spacing.spaceMedium),
                        value = songProgress,
                        onValueChange = { }
                    )
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.spaceMedium)
                    ) {
                        Text(text = currentPosition.value.toInt().asFormattedString())
                        Spacer(modifier = Modifier.weight(1f))
                        Text(musicState.value.duration.toInt().asFormattedString())
                    }
                    Spacer(modifier = Modifier.height(spacing.spaceMediumLarge))
                    MusicPlayerButtons(
                        modifier = Modifier.fillMaxWidth(),
                        playWhenReady = true,
                        play = { viewModel.play() },
                        pause = { viewModel.pause() },
                        replay10 = { },
                        forward10 = { },
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceMediumLarge))
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.comments.collect { result ->
                        if (result == null) return@collect
                        commentsAdapter.submitList(result.data)
                        when (result) {
                            is Resource.Loading -> {
                                commentsProgressBar.isVisible = true
                                commentsErrorTextView.isVisible = false
                            }
                            is Resource.Error -> {
                                commentsProgressBar.isVisible = false
                                commentsErrorTextView.isVisible = true
                            }
                            is Resource.Success -> {
                                commentsProgressBar.isVisible = false
                                commentsErrorTextView.isVisible = false
                            }
                        }
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.showDetails.collect {
                        if (it) {
                            showDetailsButton.setImageResource(R.drawable.shrink)
                            descTextView.text = song.text
                        } else {
                            showDetailsButton.setImageResource(R.drawable.expand)
                            descTextView.text =
                                song.text.subSequence(0, Integer.min(40, song.text.length - 1))
                        }
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.songEventFlow.collect { event ->
                        when (event) {
                            is SongDetailViewModel.SongEvent.SongError -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Error: ${event.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }

            songTitleTextView.text = song.title
            uploaderUsername.text = song.username
            timeTextView.text = DateFormat.getDateTimeInstance().format(song.time).dropLast(3)

            Glide.with(requireContext())
                .load(song.pfp)
                .placeholder(R.drawable.click_to_add_image)
                .into(uploaderPfpImageView)

            addCommentImageButton.setOnClickListener {
                cropImage.launch(
                    com.canhub.cropper.options {
                        setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1920, 1080)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                    }
                )
            }

            showDetailsButton.setOnClickListener {
                viewModel.onShowDetailsClick()
            }
            commentEditText.addTextChangedListener { text ->
                viewModel.commentText = text.toString()
            }

            cancelReplyingToImageButton.setOnClickListener {
                replyingToLayout.isVisible = false
                viewModel.replyingToUserName = null
                viewModel.replyingToPfp = null
                viewModel.replyingToCommentId = null
                viewModel.replyingToText = null
                viewModel.replyingToUserId = null
            }

            commentSendButton.setOnClickListener {
                hideKeyboard(requireContext())
                viewModel.onCommentSendClick(song)
                commentEditText.text.clear()
                addCommentImageView.visibility = View.GONE
            }
        }
    }

    @Composable
    fun Int.asFormattedString() = stringResource(
        id = R.string.player_timestamp_format,
        String.format(locale = Locale.US, format = "%02d", this / 60),
        String.format(locale = Locale.US, format = "%02d", this % 60)
    )

    override fun onItemLongClick(comment: Comments) {
        showKeyboard(requireContext())
        viewModel.replyingToCommentId = comment.commentId
        viewModel.replyingToUserId = comment.userId
        viewModel.replyingToPfp = comment.pfp
        viewModel.replyingToText = comment.text
        viewModel.replyingToUserName = comment.username
        binding.apply {
            replyingToLayout.isVisible = true
            replyingToTextView.text = comment.text
            replyingToUsernameTextView.text = comment.username
            Glide.with(requireContext())
                .load(comment.pfp)
                .into(replyingToPfpImageView)
        }
    }

    override fun onReplyingToClick(replyingToCommentId: String) {
        val comments = commentsAdapter.currentList
        var position = -1
        for (i in 0 until comments.size) {
            if (comments[i].commentId == replyingToCommentId) {
                position = i
                break
            }
        }
        if (position != -1) {
            binding.commentRecyclerView.scrollToPosition(position)
        }
    }


    private fun showKeyboard(mContext: Context) {
        val imm = mContext
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(requireView(), 0)
    }

    private fun hideKeyboard(mContext: Context) {
        val imm = mContext
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            requireActivity().window
                .currentFocus!!.windowToken, 0
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}