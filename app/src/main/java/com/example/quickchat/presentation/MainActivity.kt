package com.example.quickchat.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.quickchat.R
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private val viewModel by viewModels<MainViewModel>()
   // val user = FirebaseAuth.getInstance()

    override fun onStart() {
        super.onStart()
        if (viewModel.getCurrentUser()) {
            viewModel.setUserStatusOnline()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // val user = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        viewModel.setUserStatusOnline()
        if (viewModel.getCurrentUser()) {
            navGraph.setStartDestination(R.id.containerFragment)
        } else {
            navGraph.setStartDestination(R.id.registerFragment)
        }
        navController.graph = navGraph

    }

    override fun onStop() {
        super.onStop()
        viewModel.setUserStatusOffline()
    }
}