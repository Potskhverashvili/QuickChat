package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat

import android.widget.Toast
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentChatBinding
import com.example.quickchat.domain.model.ActiveUserModel
import com.example.quickchat.domain.model.UsersModel

class ChatFragment : BaseFragment<FragmentChatBinding>(FragmentChatBinding::inflate) {
    private val usersList = List(10) { index ->
        UsersModel(id = (index + 1).toString(), name = "User ${index + 1}")
    }

    private val activeUsersList = List(10) { index ->
        ActiveUserModel(id = (index + 1).toString(), name = "Online User ${index + 1}")
    }

    private lateinit var chatPageAdapter: ChatPageAdapter

    override fun viewCreated() {
        val activeUserAdapter = ActiveUserAdapter().apply {
            submitList(activeUsersList)
        }

        chatPageAdapter = ChatPageAdapter(usersList, activeUserAdapter)
        binding.recyclerViewChat.adapter = chatPageAdapter

        setListeners()
    }


    private fun setListeners() {
        chatPageAdapter.onSearchedClick = {
            Toast.makeText(requireContext(), "Search Button clicked", Toast.LENGTH_SHORT).show()
        }
    }

}
