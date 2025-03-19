package com.example.quickchat.presentation.screens.containerFragment.profile

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickchat.R
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    private val profileAdapter = ProfileAdapter()
    private val viewModel by viewModels<ProfileViewModel>()

    override fun viewCreated() {
        prepareRecyclerView()
        setListeners()
        setCollectors()
    }

    private fun prepareRecyclerView() {
        binding.recyclerViewProfile.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )
            adapter = profileAdapter
        }
    }

    private fun setListeners() = with(binding) {
        binding.btnBack.setOnClickListener {
        }

        profileAdapter.onProfileItemClick = {
            Toast.makeText(requireContext(),
                "profile was clicked", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            dialogLogOut()
        }
    }

    private fun setCollectors() {
        viewLifecycleOwner.lifecycleScope.launch(Job()) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileItems.collect { listOfItems ->
                    profileAdapter.submitList(listOfItems)
                }
            }
        }

    }

    private fun dialogLogOut() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->

                viewModel.logOutUser()
                val navController = requireActivity().findNavController(R.id.fragmentContainerView)
                navController.popBackStack(R.id.containerFragment, true)
                navController.navigate(R.id.loginFragment)

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}