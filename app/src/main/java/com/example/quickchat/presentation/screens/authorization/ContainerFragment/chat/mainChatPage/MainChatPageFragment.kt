package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.mainChatPage

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentMainChatPageBinding
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.mainChatPage.adapters.MainChatPageAdapter
import com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.mainChatPage.adapters.OnlineUserAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainChatPageFragment :
    BaseFragment<FragmentMainChatPageBinding>(FragmentMainChatPageBinding::inflate) {

    private val onlineUserAdapter = OnlineUserAdapter()
    private var mainChatPageAdapter = MainChatPageAdapter()
    private val viewModel by viewModels<MainChatPageViewModel>()

    override fun viewCreated() {
        //viewModel.setUserStatusOnline()
        prepareRecyclerView()
        fetchOnlineUsers()
        setListeners()
        setCollectors()
    }

    private fun prepareRecyclerView() {
        mainChatPageAdapter.updateUsersList(usersList)
        mainChatPageAdapter.onlineUserAdapter = onlineUserAdapter
        binding.mainChatPageRv.adapter = mainChatPageAdapter
    }

    private fun setListeners() {
        mainChatPageAdapter.onSearchedClick = {
            findNavController().navigate(MainChatPageFragmentDirections.actionChatFragmentToSearchPageFragment())
        }

        mainChatPageAdapter.onUserClick = {
            Toast.makeText(requireContext(), "User clicked", Toast.LENGTH_SHORT).show()
        }


        onlineUserAdapter.onActiveUserClick = {
            Toast.makeText(requireContext(), "ActiveUser clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.onlineUsers.collect {
                    onlineUserAdapter.submitList(it)
                }
            }
        }
    }

    private fun fetchOnlineUsers() {
        viewModel.fetchOnlineUsers()
    }



    // ----------------------------- List of Users -------------------------------------
    private val usersList = List(10) { index ->
        UsersModel(id = (index + 1).toString(), name = "User ${index + 1}")
    }

}
