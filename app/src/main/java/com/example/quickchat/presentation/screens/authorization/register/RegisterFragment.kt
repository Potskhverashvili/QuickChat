package com.example.quickchat.presentation.screens.authorization.register

import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    private val viewModel by viewModel<RegisterViewModel>()

    override fun viewCreated() {
        setListeners()
        setCollectors()
    }

    private fun setListeners() {
        binding.registerBtn.setOnClickListener {
            val userName = binding.userNameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val repeatPassword = binding.repeatPasswordEditText.text.toString()
            val isValid = viewModel.isRegistrationValid(userName, email, password, repeatPassword)
            if (isValid) {
                viewModel.registerNewUser(userName, email, password)
            }
        }

        binding.loginBtn.setOnClickListener {
            goToLogInPage()
        }
    }


    private fun setCollectors() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerFlow.collect {
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToHomeFragment())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.showError.collect { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoadingState.collect { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }

    }

    private fun goToLogInPage() {
        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
    }

}
