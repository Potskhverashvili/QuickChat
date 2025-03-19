package com.example.quickchat.presentation.screens.authorization.ContainerFragment

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.quickchat.R
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentContainerBinding

class ContainerFragment :
    BaseFragment<FragmentContainerBinding>(FragmentContainerBinding::inflate) {
    private lateinit var navController: NavController

    override fun viewCreated() {
        setUp()
    }

    private fun setUp() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentContainerView2) as? NavHostFragment
                ?: return

        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.personalChatFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }

                R.id.searchPageFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }
}