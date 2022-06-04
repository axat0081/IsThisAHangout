package com.example.isthisahangout.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.isthisahangout.R
import com.example.isthisahangout.databinding.FragmentLoginBinding
import com.example.isthisahangout.viewmodel.FirebaseAuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var mAuth: FirebaseAuth
    private val viewModel by activityViewModels<FirebaseAuthViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        binding.apply {
            emailEditText.editText!!.setText(viewModel.loginEmail)
            passwordEditText.editText!!.setText(viewModel.loginPassword)
            emailEditText.editText!!.addTextChangedListener { email ->
                viewModel.loginEmail = email.toString()
            }
            passwordEditText.editText!!.addTextChangedListener { password ->
                viewModel.loginPassword = password.toString()
            }
            loginButton.setOnClickListener {
                loginProgressBar.isVisible = true
                hideKeyboard(requireContext())
                viewModel.onLoginClick()
            }
            needNewAccountTextView.setOnClickListener {
                findNavController()
                    .navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.loginFlow.collect { event ->
                    when (event) {
                        is FirebaseAuthViewModel.AuthEvent.LoginSuccess -> {
                            loginProgressBar.isVisible = false
                            Snackbar.make(
                                requireView(),
                                event.message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(
                                LoginFragmentDirections.actionLoginFragmentToHomeFragment2()
                            )
                        }
                        is FirebaseAuthViewModel.AuthEvent.LoginFailure -> {
                            loginProgressBar.isVisible = false
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}