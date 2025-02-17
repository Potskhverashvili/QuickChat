package com.example.quickchat.presentation.screens.authorization.register

import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import okio.Timeout
import java.util.concurrent.TimeUnit


class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    private lateinit var auth: FirebaseAuth


    override fun viewCreated() {
        auth = FirebaseAuth.getInstance()
        Log.d("PhoneAuth", "Firebase Auth initialized: $auth")
        binding.btnSend.setOnClickListener {
            val phoneNumber = binding.userMobile.text.toString().trim()

            if (phoneNumber.isNotEmpty()) {
                Log.d("PhoneAuth", "User entered phone: $phoneNumber")  // Log phone number
                sendOtp(phoneNumber)
            } else {
                Toast.makeText(requireContext(), "Enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendOtp(phoneNumber: String) {
        Log.d("PhoneAuth", "Attempting to send OTP to: $phoneNumber")  // Log before sending OTP

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("PhoneAuth", "Auto-retrieval succeeded: $credential")  // Log success
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    Log.e("PhoneAuth", "Verification failed: ${exception.message}")  // Log error
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d("PhoneAuth", "OTP sent successfully. Verification ID: $verificationId")  // Log OTP sent
                    val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(verificationId)
                    findNavController().navigate(action)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}