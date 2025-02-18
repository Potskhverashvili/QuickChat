package com.example.quickchat.presentation.screens.authorization.register

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private lateinit var auth: FirebaseAuth

    override fun viewCreated() {
        auth = FirebaseAuth.getInstance()

        binding.registerBtn.setOnClickListener {
            val email = binding.userEmail.text.toString().trim()
            val password = binding.userPassword.text.toString().trim()
            val repeatPassword = binding.repeatUserPassword.text.toString().trim()

            if (areFieldsValid(email, password, repeatPassword)) {
                registerUser(email, password)
            }
        }

        binding.loginBtn.setOnClickListener {
            navigateToLoginFragment()
        }
    }

    private fun areFieldsValid(email: String, password: String, repeatPassword: String): Boolean {
        if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != repeatPassword) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    navigateToHomeFragment()
                } else {
                    Toast.makeText(requireContext(), "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToHomeFragment())
    }

    private fun navigateToLoginFragment() {
        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
    }
}
