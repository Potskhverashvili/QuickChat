package com.example.quickchat.presentation.screens.containerFragment.chat.searchPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.usecase.GetSearchedUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchPageViewModel @Inject constructor(
    private val getSearchedUsers: GetSearchedUsers
) : ViewModel() {
    private var searchJob: Job? = null

    private var _searchMovieWithQuery = MutableStateFlow<List<UsersModel>?>(null)
    val searchMovieWithQuery = _searchMovieWithQuery.asStateFlow()

    private val _showError = MutableStateFlow(false)
    val showError = _showError.asStateFlow()

    private val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    fun searchedUserWithQuery(query: String) {
        if (query.isBlank()) {
            _searchMovieWithQuery.value = null
            _showError.value = false
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isLoading.emit(true)
            _showError.value = false

            when (val status = getSearchedUsers.execute(query)) {
                is OperationStatus.Success -> {
                    val result = _searchMovieWithQuery.emit(status.value)
                }

                is OperationStatus.Failure -> {
                    _showError.value = true
                }
            }
        }
    }
}