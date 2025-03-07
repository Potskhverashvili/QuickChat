package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.personalChatPage

import android.util.Log.d
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentPersonalChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonalChatFragment :
    BaseFragment<FragmentPersonalChatBinding>(FragmentPersonalChatBinding::inflate) {
    private val args: PersonalChatFragmentArgs by navArgs()
    private val viewModel by viewModels<PersonalChatViewModel>()
    override fun viewCreated() {

        viewModel.getUserById(args.userUid)
        setCollectors()
    }


    private fun setCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userById.collect { user ->
                    d("CheckUserById", "$user")
                   // binding.userNameTv.text = user?.name

                }
            }
        }

    }
}