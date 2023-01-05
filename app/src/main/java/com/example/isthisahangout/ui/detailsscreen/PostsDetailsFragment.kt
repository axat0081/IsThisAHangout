package com.example.isthisahangout.ui.detailsscreen

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.example.isthisahangout.R
import com.example.isthisahangout.adapter.CommentsAdapter
import com.example.isthisahangout.databinding.FragmentPostDetailsBinding
import com.example.isthisahangout.models.Comments
import com.example.isthisahangout.models.FirebasePost
import com.example.isthisahangout.utils.Resource
import com.example.isthisahangout.viewmodel.detailScreen.PostDetailsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat


@AndroidEntryPoint
class PostsDetailsFragment : Fragment(R.layout.fragment_post_details),
    CommentsAdapter.OnItemLongClickListener, CommentsAdapter.OnReplyingToClickListener {
    private var _binding: FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PostDetailsViewModel>()
    private lateinit var cropImage: ActivityResultLauncher<CropImageContractOptions>
    private lateinit var commentsAdapter: CommentsAdapter
    private val args by navArgs<PostsDetailsFragmentArgs>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val post: FirebasePost = args.post
        _binding = FragmentPostDetailsBinding.bind(view)
        commentsAdapter = CommentsAdapter(this)
        cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                val uri = result.uriContent
                viewModel.commentImage = uri
                binding.addCommentImageView.visibility = VISIBLE
                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.addCommentImageView)
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
            addCommentImageView.visibility = GONE
            bookmarkImageView.setImageResource(R.drawable.bookmark)

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.isLiked.collect {
                        Log.e("post", it.toString())
                        likeButton.isLiked = it
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.currentPost.collect { post ->
                        likeTextView.text = (post?.likes ?: 0).toString()
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.isBookMarked.collect {
                        bookmarkImageView.setImageResource(
                            if (it)
                                R.drawable.bookmarked
                            else R.drawable.bookmark
                        )
                    }
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

            commentRecyclerview.apply {
                isVisible = true
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = commentsAdapter
                itemAnimator = null
            }

            Glide.with(requireContext())
                .load(post.pfp)
                .placeholder(R.drawable.click_to_add_image)
                .into(posterPfpImageView)
            postTitleTextView.text = post.title!!
            postBody.text = post.text
            posterUsername.text = post.username
            timeTextView.text = DateFormat.getDateTimeInstance().format(post.time).dropLast(3)
            postImageView.isClickable = false
            Glide.with(requireContext())
                .load(post.image)
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
                        postImageView.isClickable = true
                        return false
                    }
                }).into(postImageView)

            bookmarkImageView.setOnClickListener {
                viewModel.onBookMarkClick()
            }

            addCommentImageButton.setOnClickListener {
                cropImage.launch(
                    com.canhub.cropper.options {
                        setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1920, 1080)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                    }
                )
            }

            likeButton.setOnClickListener {
                viewModel.onLikeClick()
            }

            commentEditText.addTextChangedListener { text ->
                viewModel.commentText = text.toString()
            }

            commentSendButton.setOnClickListener {
                hideKeyboard(requireContext())
                addCommentImageView.isVisible = false
                viewModel.onCommentSendClick(post)
                commentEditText.text.clear()
                addCommentImageView.isVisible = false
            }

            cancelReplyingToImageButton.setOnClickListener {
                replyingToLayout.isVisible = false
                viewModel.replyingToUserName = null
                viewModel.replyingToPfp = null
                viewModel.replyingToCommentId = null
                viewModel.replyingToText = null
                viewModel.replyingToUserId = null
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.postsCommentEventFlow.collect { event ->
                        when (event) {
                            is PostDetailsViewModel.PostsCommentEvent.CommentSendFailure -> {
                                Snackbar.make(
                                    requireView(), event.message, Snackbar.LENGTH_SHORT
                                )
                            }
                            is PostDetailsViewModel.PostsCommentEvent.CommentSentSuccess -> Unit
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.postsLikeBookMarkEventFlow.collect { event ->
                        when (event) {
                            is PostDetailsViewModel.PostsLikeBookMarkEvent.AddedToBookMarks -> {
                                Snackbar.make(
                                    requireView(), "Added to bookmarks", Snackbar.LENGTH_SHORT
                                )
                            }
                            is PostDetailsViewModel.PostsLikeBookMarkEvent.RemovedFromBookMarks -> {
                                Snackbar.make(
                                    requireView(), "Removed from bookmarks", Snackbar.LENGTH_SHORT
                                )
                            }
                            is PostDetailsViewModel.PostsLikeBookMarkEvent.PostsLikeError -> {
                                Snackbar.make(
                                    requireView(), event.message, Snackbar.LENGTH_SHORT
                                )
                            }
                        }
                    }
                }
            }
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

    override fun onItemLongClick(comment: Comments) {
        showKeyboard(requireContext())
        binding.commentRecyclerview.scrollToPosition(0)
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
            binding.commentRecyclerview.scrollToPosition(position)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}