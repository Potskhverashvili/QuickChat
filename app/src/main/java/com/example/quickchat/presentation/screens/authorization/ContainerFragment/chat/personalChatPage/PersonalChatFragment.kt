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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
                val chatData = mapOf(
                    "chatId" to chatId,
                    "users" to listOf(currentUserUid, otherUserUid),
                    "createdAt" to System.currentTimeMillis()
                )
                chatRef.setValue(chatData).addOnFailureListener {
                    Log.e("Chat", "Failed to create chat: ${it.message}")
                }
            }

            val generalMessagesRef = chatRef.child("generalMessages")
            generalMessagesRef.get().addOnSuccessListener { messagesSnapshot ->
                if (!messagesSnapshot.exists()) {
                    generalMessagesRef.setValue(emptyMap<String, Any>())
                        .addOnSuccessListener { Log.d("Chat", "General messages node created!") }
                }
            }
        }.addOnFailureListener {
            Log.e("Chat", "Error checking chat existence: ${it.message}")
        }

        listenForMessages(chatId)
        setListeners()
    }

    private fun generateChatId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "${uid1}_${uid2}" else "${uid2}_${uid1}"
    }

    private fun prepareRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true  // Makes sure the RecyclerView starts from the bottom

        binding.messagesRecyclerView.layoutManager = layoutManager
        binding.messagesRecyclerView.adapter = personalAdapter
    }

    private fun setListeners() = with(binding) {
        sendButton.setOnClickListener {
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
        val chatId = generateChatId(currentUserUid, otherUserUid)
        val chatRef = firebaseDatabase.child(chatId).child("generalMessages")
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: return


        val messageId = UUID.randomUUID().toString()
        val messageData = mapOf(
            "id" to messageId,
            "text" to message.text,
            "senderUid" to currentUserUid,
            "senderEmail" to currentUserEmail,  // Save sender's email
            "timestamp" to System.currentTimeMillis()
        )

        chatRef.child(messageId).setValue(messageData)
            .addOnSuccessListener {
                Log.d("Chat", "Message sent successfully!")
                binding.messagesRecyclerView.scrollToPosition(personalAdapter.itemCount - 1)
            }
            .addOnFailureListener { Log.e("Chat", "Failed to send message: ${it.message}") }
    }

    private fun listenForMessages(chatId: String) {
        val chatRef = firebaseDatabase.child(chatId).child("generalMessages")

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<MessageModel>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(MessageModel::class.java)
                    message?.let { messages.add(it) }
                }
                personalAdapter.setMessages(messages)

                // Scroll to the last item after setting the messages
                binding.messagesRecyclerView.scrollToPosition(messages.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Chat", "Failed to load messages: ${error.message}")
            }
        })
    }
}