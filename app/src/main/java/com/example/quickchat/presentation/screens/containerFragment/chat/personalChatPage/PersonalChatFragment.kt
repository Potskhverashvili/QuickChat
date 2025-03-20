package com.example.quickchat.presentation.screens.containerFragment.chat.personalChatPage

import android.view.View
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
    private val personalAdapter = PersonalChatAdapter()

    override fun viewCreated() {
        viewModel.createOrGetChatSession(args.receiverUid)
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
            sendMessage()
            messageEditText.text?.clear()
        }
    }

    private fun sendMessage() {
        val chatId = viewModel.chatId.value
        val messageText = binding.messageEditText.text.toString().trim()

        if (chatId != null) {
            viewModel.sendMessage(chatId, messageText)
        } else {
            Toast.makeText(requireContext(), "Chat not initialized yet.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCollectors() {
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
                viewModel.receiverName.collect { personToMessageName ->
                    binding.userNameTv.text = personToMessageName.name.toString()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadingState.collect { loading ->
                    if (loading) {
                        loadingVisibleTrue()
                    } else {
                        loadingInvisibleFalse()
                    }
                }
            }
        }

    }


    private fun loadingVisibleTrue() = with(binding) {
        progressBar.visibility = View.VISIBLE
        backBtn.visibility = View.INVISIBLE
        userCircularImage.visibility = View.INVISIBLE
        userInfoContainer.visibility = View.INVISIBLE
        callBtn.visibility = View.INVISIBLE
        messagesRecyclerView.visibility = View.INVISIBLE
        messageEditText.visibility = View.INVISIBLE
        sendButton.visibility = View.INVISIBLE
    }

    private fun loadingInvisibleFalse() = with(binding) {
        progressBar.visibility = View.INVISIBLE
        backBtn.visibility = View.VISIBLE
        userCircularImage.visibility = View.VISIBLE
        userInfoContainer.visibility = View.VISIBLE
        callBtn.visibility = View.VISIBLE
        messagesRecyclerView.visibility = View.VISIBLE
        messageEditText.visibility = View.VISIBLE
        sendButton.visibility = View.VISIBLE
    }

}