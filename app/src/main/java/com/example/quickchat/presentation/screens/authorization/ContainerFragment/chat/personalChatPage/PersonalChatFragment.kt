package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.personalChatPage

import android.util.Log
import android.util.Log.d
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickchat.core.BaseFragment
import com.example.quickchat.databinding.FragmentPersonalChatBinding
import com.example.quickchat.domain.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class PersonalChatFragment :
    BaseFragment<FragmentPersonalChatBinding>(FragmentPersonalChatBinding::inflate) {
    private val args: PersonalChatFragmentArgs by navArgs()
    private val viewModel by viewModels<PersonalChatViewModel>()
    private val curUser = FirebaseAuth.getInstance()
    private val personalAdapter = PersonalChatAdapter()

    private val firebaseDatabase = FirebaseDatabase
        .getInstance("https://quickchat-d765e-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("messages")

    override fun viewCreated() {
        prepareRecyclerView()
        val currentUserUid = curUser.uid ?: return
        val otherUserUid = args.userUid

        val chatId = generateChatId(currentUserUid, otherUserUid)
        val chatRef = firebaseDatabase.child(chatId)

        chatRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                // ✅ Create chat entry if it doesn't exist
                val chatData = mapOf(
                    "chatId" to chatId,
                    "users" to listOf(currentUserUid, otherUserUid),
                    "createdAt" to System.currentTimeMillis()
                )
                chatRef.setValue(chatData).addOnSuccessListener {
                    Log.d("Chat", "Chat created successfully!")
                }.addOnFailureListener {
                    Log.e("Chat", "Failed to create chat: ${it.message}")
                }
            }

            // ✅ Ensure `generalMessages` node exists inside the chat
            val generalMessagesRef = chatRef.child("generalMessages")
            generalMessagesRef.get().addOnSuccessListener { messagesSnapshot ->
                if (!messagesSnapshot.exists()) {
                    generalMessagesRef.setValue(emptyMap<String, Any>()).addOnSuccessListener {
                        Log.d("Chat", "General messages node created!")
                    }
                }
            }
        }.addOnFailureListener {
            Log.e("Chat", "Error checking chat existence: ${it.message}")
        }
        setListeners()
    }

    private fun generateChatId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "${uid1}_${uid2}" else "${uid2}_${uid1}"
    }


    private fun prepareRecyclerView() {
        binding.messagesRecyclerView.apply {
            adapter = personalAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setListeners() = with(binding) {
        binding.sendButton.setOnClickListener {
            sendMessage(
                MessageModel(
                    id = UUID.randomUUID().toString(),
                    text = messageEditText.text.toString(),
                    senderEmail = FirebaseAuth.getInstance().currentUser?.email
                )
            )
            messageEditText.text.clear()
        }
    }

    private fun sendMessage(message: MessageModel) {
        val currentUserUid = curUser.uid ?: return
        val otherUserUid = args.userUid
        val chatId = generateChatId(currentUserUid, otherUserUid) // Get the correct chatId

        val chatRef = firebaseDatabase.child(chatId).child("generalMessages") // Navigate to generalMessages

        val messageId = UUID.randomUUID().toString()
        val messageData = mapOf(
            "id" to messageId,
            "text" to message.text,
            "senderUid" to currentUserUid,
            "timestamp" to System.currentTimeMillis()
        )

        chatRef.child(messageId).setValue(messageData)
            .addOnSuccessListener { Log.d("Chat", "Message sent successfully!") }
            .addOnFailureListener { Log.e("Chat", "Failed to send message: ${it.message}") }
    }

}