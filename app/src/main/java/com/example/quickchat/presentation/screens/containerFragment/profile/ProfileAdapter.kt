package com.example.quickchat.presentation.screens.containerFragment.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickchat.databinding.ItemProfileBinding
import com.example.quickchat.databinding.ItemProfileHeaderBinding

class ProfileAdapter : ListAdapter<ProfileItem, RecyclerView.ViewHolder>(ProfileDiffCallback()) {

    var onProfileItemClick: (profile: ProfileItem) -> Unit = {}

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            ProfileItemType.HEADER -> VIEW_TYPE_HEADER
            ProfileItemType.ITEM -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemProfileHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }

            VIEW_TYPE_ITEM -> {
                val binding = ItemProfileBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ItemViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item.type) {
            ProfileItemType.HEADER -> (holder as HeaderViewHolder).bind(item)
            ProfileItemType.ITEM -> (holder as ItemViewHolder).bind(item)
        }
    }

    inner class HeaderViewHolder(private val binding: ItemProfileHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProfileItem) = with(binding) {
            Glide.with(userImage)
                .load(item.userImage)
                .into(userImage)
            userName.text = item.userName
            userEmail.text = item.userEmail

            binding.btnProfileImageEdit.setOnClickListener {
                onProfileItemClick.invoke(item)
            }
        }
    }

    inner class ItemViewHolder(private val binding: ItemProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProfileItem) = with(binding) {
            Glide.with(image)
                .load(item.icon)
                .into(image)
            textview.text = item.title
        }
    }

    class ProfileDiffCallback : DiffUtil.ItemCallback<ProfileItem>() {
        override fun areItemsTheSame(oldItem: ProfileItem, newItem: ProfileItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProfileItem, newItem: ProfileItem): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}
