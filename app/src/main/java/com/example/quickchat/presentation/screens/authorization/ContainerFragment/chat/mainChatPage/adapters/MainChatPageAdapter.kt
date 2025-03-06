package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.mainChatPage.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quickchat.databinding.ItemOnlineUsersRvBinding
import com.example.quickchat.databinding.ItemSearchBinding
import com.example.quickchat.databinding.ItemUserBinding
import com.example.quickchat.domain.model.UsersModel

class MainChatPageAdapter()
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onlineUserAdapter = OnlineUserAdapter()
    private var userList: List<UsersModel> = emptyList()

    fun updateUsersList(newUsersList: List<UsersModel>) {
        val userListCallBack = UserListCallBack(userList, newUsersList)
        val diffResult = DiffUtil.calculateDiff(userListCallBack)
        userList = newUsersList
        diffResult.dispatchUpdatesTo(this)
    }


    var onSearchedClick: () -> Unit = {}
    var onUserClick: () -> Unit = {}


    inner class ActiveUsersViewHolder(private val binding: ItemOnlineUsersRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.onlineUsersRecyclerView.adapter = onlineUserAdapter
        }
    }

    inner class UsersViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userModel: UsersModel) {
            binding.itemUser.text = userModel.name
            binding.root.setOnClickListener {
                onUserClick.invoke()
            }
        }
    }

    inner class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.searchBtn.setOnClickListener {
                onSearchedClick.invoke()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            VIEW_TYPE_ACTIVE_USERS -> {
                val binding = ItemOnlineUsersRvBinding.inflate(
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

    override fun getItemCount() = userList.size + 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ActiveUsersViewHolder -> holder.bind()
            is SearchViewHolder -> holder.bind()
            is UsersViewHolder -> holder.bind(userList[position - 2])
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

    class UserListCallBack(
        private val oldList: List<UsersModel>,
        private val newList: List<UsersModel>

    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldList = oldList[oldItemPosition]
            val newList = newList[newItemPosition]
            return oldList.id == newList.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldList = oldList[oldItemPosition]
            val newList = newList[newItemPosition]
            return oldList == newList
        }
    }
}