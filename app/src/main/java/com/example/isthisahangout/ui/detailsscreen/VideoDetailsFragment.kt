package com.example.isthisahangout.ui.detailsscreen

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.ui.PlayerView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.example.isthisahangout.MainActivity
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.CommentsAdapter
import com.example.isthisahangout.databinding.FragmentVideoDetailsBinding
import com.example.isthisahangout.models.Comments
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.utils.observeFlows
import com.example.isthisahangout.viewmodel.detailScreen.VideoDetailViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Integer.min
import java.text.DateFormat

@AndroidEntryPoint
class VideoDetailsFragment : Fragment(R.layout.fragment_video_details),
    CommentsAdapter.OnItemLongClickListener, CommentsAdapter.OnReplyingToClickListener, PlayerView.FullscreenButtonClickListener {
    private var _binding: FragmentVideoDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<VideoDetailViewModel>()
    private val args by navArgs<VideoDetailsFragmentArgs>()
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var cropImage: ActivityResultLauncher<CropImageContractOptions>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVideoDetailsBinding.bind(view)
        val video = args.video
        commentsAdapter = CommentsAdapter(this)
        cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                val uri = result.uriContent
                viewModel.setCommentImage(uri)
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
            commentRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = commentsAdapter
                itemAnimator = null
                isVisible = true
            }
            playerView.player = viewModel.player
            observeFlows { coroutineScope ->
                coroutineScope.launch {
                    viewModel.commentImage.collect { image->
                        if(image == null) {
                            addCommentImageView.visibility = GONE
                        } else {
                            addCommentImageView.visibility = VISIBLE
                            Glide.with(requireContext())
                                .load(image.toUri())
                                .into(binding.addCommentImageView)
                        }
                    }
                }
                coroutineScope.launch {
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
                coroutineScope.launch {
                    viewModel.isBookMarked.collect {
                        bookmarkImageView.setImageResource(
                            if (it) R.drawable.bookmarked
                            else R.drawable.bookmark
                        )
                    }
                }
                coroutineScope.launch {
                    viewModel.isLiked.collect {
                        likeButton.isLiked = it
                    }
                }
                coroutineScope.launch {
                    viewModel.showDetails.collect {
                        if (it) {
                            showDetailsButton.setImageResource(R.drawable.shrink)
                            descTextView.text = video.text!!
                        } else {
                            showDetailsButton.setImageResource(R.drawable.expand)
                            descTextView.text =
                                video.text!!.subSequence(0, min(40, video.text.length - 1))
                        }
                    }
                }
                coroutineScope.launch {
                    viewModel.currentVideo.collect { video->
                        likeTextView.text = (video?.likes?:0).toString()
                    }
                }
            }

            if (video.text == null) {
                showDetailsButton.isClickable = false
                showDetailsButton.isVisible = false
            }

            showDetailsButton.setOnClickListener {
                viewModel.onShowDetailsClick()
            }

            bookmarkImageView.setOnClickListener {
                viewModel.onBookMarkClick()
            }

            likeButton.setOnClickListener {
                viewModel.onLikeClick()
            }

            videoTitleTextView.text = video.title
            uploaderUsername.text = video.username
            timeTextView.text = DateFormat.getDateTimeInstance().format(video.time).dropLast(3)

            Glide.with(requireContext())
                .load(video.pfp)
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
                viewModel.onCommentSendClick(video)
                commentEditText.text.clear()
                addCommentImageView.visibility = GONE
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.videosLikeBookMarkEventFlow.collect { event ->
                        when (event) {
                            is VideoDetailViewModel.VideoLikeBookMarkEvent.AddedToBookMarks -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Added to BookMarks",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is VideoDetailViewModel.VideoLikeBookMarkEvent.RemovedFromBookMarks -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Removed from BookMarks",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is VideoDetailViewModel.VideoLikeBookMarkEvent.VideosLikeError -> {
                                Toast.makeText(
                                    requireContext(),
                                    event.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.videosCommentEventFlow.collect { event ->
                        when (event) {
                            is VideoDetailViewModel.VideosCommentEvent.CommentSendFailure -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Comment could not be sent: ${event.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is VideoDetailViewModel.VideosCommentEvent.CommentSentSuccess -> Unit
                        }
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            (requireActivity() as MainActivity).supportActionBar!!.hide()
        } else {
            (requireActivity() as MainActivity).supportActionBar!!.show()
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
                .currentFocus?.windowToken, 0
        )
    }

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

    override fun onFullscreenButtonClick(isFullScreen: Boolean) {

    }

    override fun onResume() {
        super.onResume()
        binding.playerView.onResume()
        binding.playerView.player?.play()
    }

    override fun onPause() {
        super.onPause()
        binding.playerView.onPause()
        binding.playerView.player?.pause()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}