package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.mainChatPage

import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentMainChatPageBinding
import com.example.quickchat.domain.model.OnlineUserModel
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.mainChatPage.adapters.MainChatPageAdapter
import com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.mainChatPage.adapters.OnlineUserAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainChatPageFragment :
    BaseFragment<FragmentMainChatPageBinding>(FragmentMainChatPageBinding::inflate) {

    private val viewModel by viewModels<MainChatPageViewModel>()
    private val onlineUserAdapter = OnlineUserAdapter()
    private var mainChatPageAdapter = MainChatPageAdapter()

    override fun viewCreated() {
        prepareRecyclerView()
        setListeners()
        viewModel.getOnlineUsers()
    }


    private fun prepareRecyclerView() {
        onlineUserAdapter.submitList(activeUsersList)
        mainChatPageAdapter.updateUsersList(usersList)
        mainChatPageAdapter.onlineUserAdapter = onlineUserAdapter
        binding.mainChatPageRv.adapter = mainChatPageAdapter
    }

    private fun setListeners() {
        mainChatPageAdapter.onSearchedClick = {
            Toast.makeText(requireContext(), "Search Button clicked", Toast.LENGTH_SHORT).show()
        }

        mainChatPageAdapter.onUserClick = {
            Toast.makeText(requireContext(), "User clicked", Toast.LENGTH_SHORT).show()
        }

        onlineUserAdapter.onActiveUserClick = {
            Toast.makeText(requireContext(), "ActiveUser clicked", Toast.LENGTH_SHORT).show()
        }
    }


    // ----------------------------- List of Users -------------------------------------
    private val usersList = List(10) { index ->
        UsersModel(id = (index + 1).toString(), name = "User ${index + 1}")
    }

    private val activeUsersList = List(10) { index ->
        OnlineUserModel(id = (index + 1).toString(), name = "Online User ${index + 1}")
    }

}
