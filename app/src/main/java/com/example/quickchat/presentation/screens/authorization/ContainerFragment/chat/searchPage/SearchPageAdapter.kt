package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.searchPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quickchat.databinding.ItemSearchedPersonBinding
import com.example.quickchat.domain.model.UsersModel

class SearchPageAdapter :
    ListAdapter<UsersModel, SearchPageAdapter.SearchPageViewHolder>(SearchPageDiffUtil()) {

    var onSearchedUserClicked:(UsersModel)-> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPageViewHolder {
        return SearchPageViewHolder(
            ItemSearchedPersonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchPageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchPageViewHolder(private val binding: ItemSearchedPersonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UsersModel) = with(binding) {
            searchedUserName.text = user.name
            root.setOnClickListener {
                onSearchedUserClicked.invoke(user)
            }
        }
    }

    class SearchPageDiffUtil() : DiffUtil.ItemCallback<UsersModel>() {
        override fun areItemsTheSame(oldItem: UsersModel, newItem: UsersModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UsersModel, newItem: UsersModel): Boolean =
            oldItem == newItem
    }

}