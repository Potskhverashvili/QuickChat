package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.personalChatPage


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quickchat.databinding.ItemMessageReceivedBinding
import com.example.quickchat.databinding.ItemMessageSentBinding
import com.example.quickchat.domain.model.MessageModel
import com.google.firebase.auth.FirebaseAuth

class PersonalChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messages = emptyList<MessageModel>()

    fun setMessages(newMessages: List<MessageModel>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    // -------------------------------- Override Methods ----------------------------------
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderEmail == FirebaseAuth.getInstance().currentUser?.email) {
            SENT_MESSAGE_TYPE
        } else {
            RECEIVED_MESSAGE_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENT_MESSAGE_TYPE) {
            SentMessageViewHolder(
                ItemMessageSentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ReceivedMessageViewHolder(
                ItemMessageReceivedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SentMessageViewHolder -> holder.bind(messages[position])
            is ReceivedMessageViewHolder -> holder.bind(messages[position])
        }
    }

    class ReceivedMessageViewHolder(private val binding: ItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageModel) {
            binding.emailTextView.text = message.senderEmail
            binding.messageTextView.text = message.text
        }
    }

    class SentMessageViewHolder(private val binding: ItemMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageModel) {
            binding.emailTextView.text = message.senderEmail
            binding.messageTextView.text = message.text
        }
    }

    companion object {
        const val SENT_MESSAGE_TYPE = 1
        const val RECEIVED_MESSAGE_TYPE = 2
    }

}