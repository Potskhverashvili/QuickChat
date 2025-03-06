package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.mainChatPage.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quickchat.databinding.ItemOnlineUserBinding
import com.example.quickchat.domain.model.UsersModel

class OnlineUserAdapter :
    ListAdapter<UsersModel, OnlineUserAdapter.OnlineUserViewHolder>(OnlineUserItemCallBack()) {

    var onActiveUserClick: () -> Unit = {}

    inner class OnlineUserViewHolder(private val binding: ItemOnlineUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(onlineUser: UsersModel) {
            binding.activeUserTv.text = onlineUser.name
            binding.root.setOnClickListener {
                onActiveUserClick.invoke()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OnlineUserViewHolder(
        ItemOnlineUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: OnlineUserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class OnlineUserItemCallBack : DiffUtil.ItemCallback<UsersModel>() {
        override fun areItemsTheSame(oldItem: UsersModel, newItem: UsersModel) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UsersModel, newItem: UsersModel) =
            oldItem == newItem
    }
}