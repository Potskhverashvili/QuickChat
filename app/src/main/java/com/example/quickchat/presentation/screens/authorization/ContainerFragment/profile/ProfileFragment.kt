package com.example.quickchat.presentation.screens.authorization.ContainerFragment.profile

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

        btnLogout.setOnClickListener {
            dialogLogOut()
        }
    }

    private fun setCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileItems.collect { listOfItems ->
                    profileAdapter.submitList(listOfItems)
                    Log.d("PROFILE","$listOfItems")
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

                /*Toast.makeText(requireContext(),
                    "Log Out", Toast.LENGTH_SHORT).show()*/
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}