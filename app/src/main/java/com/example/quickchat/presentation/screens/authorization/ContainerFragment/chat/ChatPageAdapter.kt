package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quickchat.databinding.ItemActiveUsersRvBinding
import com.example.quickchat.databinding.ItemSearchBinding
import com.example.quickchat.databinding.ItemUserBinding
import com.example.quickchat.domain.model.UsersModel

class ChatPageAdapter(
    private val usersList: List<UsersModel>,
    private val activeUsersAdapter: ActiveUserAdapter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onSearchedClick: () -> Unit = {}

    inner class ActiveUsersViewHolder(private val binding: ItemActiveUsersRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.activeUsersRecyclerView.adapter = activeUsersAdapter
        }
    }

    class UsersViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userModel: UsersModel) {
            binding.itemUser.text = userModel.name
        }
    }

    inner class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnClickListener {
                onSearchedClick.invoke()
            }
        }
    }

    //----------------------------------------------------------------------

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            VIEW_TYPE_ACTIVE_USERS -> {
                val binding = ItemActiveUsersRvBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ActiveUsersViewHolder(binding)
            }

            VIEW_TYPE_SEARCH -> {
                val binding =
                    ItemSearchBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                SearchViewHolder(binding)
            }

            VIEW_TYPE_USERS -> {
                val binding = ItemUserBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false

                )
                UsersViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Wrong ViewType was found: $viewType")
        }

    }

    override fun getItemCount() = usersList.size + 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ActiveUsersViewHolder -> holder.bind()
            is UsersViewHolder -> holder.bind(usersList[position - 2])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_ACTIVE_USERS
            1 -> VIEW_TYPE_SEARCH
            else -> VIEW_TYPE_USERS
        }
    }

    companion object {
        const val VIEW_TYPE_ACTIVE_USERS = 0
        const val VIEW_TYPE_SEARCH = 1
        const val VIEW_TYPE_USERS = 2
    }
}