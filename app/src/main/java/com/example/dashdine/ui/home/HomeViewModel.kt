package com.example.dashdine.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dashdine.data.model.Store
import com.example.dashdine.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val stores: List<Store> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val location: String = "宜宾翠屏区"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadStores()
    }

    private fun loadStores() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val stores = repository.getStores()
            _uiState.value = _uiState.value.copy(
                stores = stores,
                isLoading = false
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    /**
     * 根据搜索关键词过滤店铺
     */
    fun getFilteredStores(): List<Store> {
        val state = _uiState.value
        return if (state.searchQuery.isBlank()) {
            state.stores
        } else {
            state.stores.filter {
                it.name.contains(state.searchQuery, ignoreCase = true) ||
                it.tags.any { tag -> tag.contains(state.searchQuery, ignoreCase = true) }
            }
        }
    }
}
