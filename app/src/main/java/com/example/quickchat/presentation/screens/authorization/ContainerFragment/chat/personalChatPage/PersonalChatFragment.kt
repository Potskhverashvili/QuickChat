package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.personalChatPage

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentPersonalChatBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonalChatFragment :
    BaseFragment<FragmentPersonalChatBinding>(FragmentPersonalChatBinding::inflate) {
    private val args: PersonalChatFragmentArgs by navArgs()
    private val viewModel by viewModels<PersonalChatViewModel>()
    private val curUser = FirebaseAuth.getInstance()
    private val personalAdapter = PersonalChatAdapter()


    override fun viewCreated() {
        curUser.uid?.let { viewModel.createOrGetChatSession(it, args.userUid) }
        prepareRecyclerView()
        setListeners()
        setCollectors()
    }

    private fun prepareRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.messagesRecyclerView.layoutManager = layoutManager
        binding.messagesRecyclerView.adapter = personalAdapter
    }

    private fun setListeners() {
        btnSentMessageClick()
    }

    private fun btnSentMessageClick() = with(binding) {
        sendButton.setOnClickListener {
            startMessaging()
            messageEditText.text.clear()
        }
    }

    private fun startMessaging() {
        val chatId = viewModel.chatId.value
        val senderEmail = curUser.currentUser?.email // Get the sender's email
        val messageText = binding.messageEditText.text.toString()
        val senderUid = curUser.uid
        if (chatId != null) {
            if (senderEmail != null) {
                if (senderUid != null) {
                    viewModel.sendMessageAndGetAllMessages(chatId, senderEmail, senderUid, messageText)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Chat not initialized yet.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chatId.collect { chatId ->
                    if (chatId != null) {

                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messages.collect { messages ->
                    personalAdapter.setMessages(messages)
                    binding.messagesRecyclerView.post {
                        binding.messagesRecyclerView.scrollToPosition(messages.size - 1)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadingState.collect { isLoading ->
                    // Show a loading indicator or something else based on the loading state
                    if (isLoading) {
                        // Show loading state (e.g., a progress bar)
                    } else {
                        // Hide loading state
                    }
                }
            }
        }

    }

}