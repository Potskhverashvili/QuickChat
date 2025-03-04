package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.mainChatPage

import android.widget.Toast
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentMainChatPageBinding
import com.example.quickchat.domain.model.ActiveUserModel
import com.example.quickchat.domain.model.UsersModel

class MainChatPageFragment :
    BaseFragment<FragmentMainChatPageBinding>(FragmentMainChatPageBinding::inflate) {

    private val activeUserAdapter = ActiveUserAdapter()
    private var mainChatPageAdapter = MainChatPageAdapter()

    override fun viewCreated() {
        prepareRecyclerView()
        setListeners()
    }

    private fun prepareRecyclerView() {
        activeUserAdapter.submitList(activeUsersList)
        mainChatPageAdapter.updateUsersList(usersList)
        mainChatPageAdapter.activeUserAdapter = activeUserAdapter
        binding.mainChatPageRv.adapter = mainChatPageAdapter
    }

    private fun setListeners() {
        mainChatPageAdapter.onSearchedClick = {
            Toast.makeText(requireContext(), "Search Button clicked", Toast.LENGTH_SHORT).show()
        }

        mainChatPageAdapter.onUserClick = {
            Toast.makeText(requireContext(), "User clicked", Toast.LENGTH_SHORT).show()
        }

        activeUserAdapter.onActiveUserClick = {
            Toast.makeText(requireContext(), "ActiveUser clicked", Toast.LENGTH_SHORT).show()
        }
    }


    // ----------------------------- List of Users -------------------------------------
    private val usersList = List(10) { index ->
        UsersModel(id = (index + 1).toString(), name = "User ${index + 1}")
    }

    private val activeUsersList = List(10) { index ->
        ActiveUserModel(id = (index + 1).toString(), name = "Online User ${index + 1}")
    }

}
