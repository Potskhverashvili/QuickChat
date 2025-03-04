package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.mainChatPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quickchat.databinding.ItemActiveUserBinding
import com.example.quickchat.domain.model.ActiveUserModel

class ActiveUserAdapter :
    ListAdapter<ActiveUserModel, ActiveUserAdapter.ActiveUserViewHolder>(ActiveUserItemCallBack()) {

    var onActiveUserClick: () -> Unit = {}

    inner class ActiveUserViewHolder(private val binding: ItemActiveUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(activeUser: ActiveUserModel) {
            binding.activeUserTv.text = activeUser.name
            binding.root.setOnClickListener{
                onActiveUserClick.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ActiveUserViewHolder(
        ItemActiveUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ActiveUserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class ActiveUserItemCallBack : DiffUtil.ItemCallback<ActiveUserModel>() {
        override fun areItemsTheSame(oldItem: ActiveUserModel, newItem: ActiveUserModel) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ActiveUserModel, newItem: ActiveUserModel) =
            oldItem == newItem
    }
}