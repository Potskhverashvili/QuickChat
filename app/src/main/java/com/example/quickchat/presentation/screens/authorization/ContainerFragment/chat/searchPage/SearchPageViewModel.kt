package com.example.quickchat.presentation.screens.authorization.ContainerFragment.chat.searchPage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickchat.core.OperationStatus
import com.example.quickchat.domain.model.UsersModel
import com.example.quickchat.domain.usecase.GetSearchedUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _noUserFound = MutableStateFlow(false)
    val noMoviesFound = _noUserFound.asStateFlow()

    private val _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    fun searchedMovieWithQuery(query: String) {
        if (query.isBlank()) {
            _searchMovieWithQuery.value = null
            _noUserFound.value = false
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isLoading.emit(true)
            _noUserFound.value = false

            when (val status = getSearchedUsers.execute(query)) {
                is OperationStatus.Success -> {
                    val result = _searchMovieWithQuery.emit(status.value)
                    Log.d("MYLOGING","result = ${searchMovieWithQuery.value}")
                }

                is OperationStatus.Failure -> {
                    _noUserFound.value = true
                }

                is OperationStatus.Loading -> {

                }
            }

        }
    }
}