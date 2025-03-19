package com.example.quickchat.presentation.screens.containerFragment.chat.searchPage

import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentSearchPageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchPageFragment :
    BaseFragment<FragmentSearchPageBinding>(FragmentSearchPageBinding::inflate) {
    private val viewModel by viewModels<SearchPageViewModel>()
    private val searchPageAdapter = SearchPageAdapter()

    override fun viewCreated() {
        prepareRecyclerView()
        setListeners()
        setCollectors()
    }

    private fun prepareRecyclerView() {
        binding.searchUserRecyclerView.apply {
            adapter = searchPageAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setListeners() {
        binding.btnSearch.doAfterTextChanged { newInputQuery ->
            viewModel.searchedUserWithQuery(newInputQuery.toString())
        }

        searchPageAdapter.onSearchedUserClicked = { user->
            findNavController().navigate(SearchPageFragmentDirections.actionSearchPageFragmentToPersonalChatFragment(user.id))
        }
    }

    private fun setCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchMovieWithQuery.collect { foundUsersList ->
                    searchPageAdapter.submitList(foundUsersList)
                }
            }
        }
    }
}