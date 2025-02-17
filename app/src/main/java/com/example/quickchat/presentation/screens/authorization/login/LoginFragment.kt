package com.example.quickchat.presentation.screens.authorization.login

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val args: LoginFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth

    override fun viewCreated() {
        auth = FirebaseAuth.getInstance()

        binding.codeNumbers.setOnEditorActionListener { _, _, _ ->
            val code = binding.codeNumbers.text.toString().trim()
            if (code.isNotEmpty()) {
                verifyCode(code)
            } else {
                Toast.makeText(requireContext(), "Enter the 6-digit code", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(args.verificationId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                } else {
                    Toast.makeText(requireContext(), "Invalid OTP!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}