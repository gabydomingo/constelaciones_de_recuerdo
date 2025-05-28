package com.example.constelaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.constelaciones.data.model.NasaEvent
import com.example.constelaciones.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*


class TimelineViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<NasaEvent>>(emptyList())
    val events = _events.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredEvents = combine(_events, _searchQuery) { events, query ->
        if (query.isBlank()) {
            events
        } else {
            events.filter { it.title.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchAstronomyEvents()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun fetchAstronomyEvents() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.nasaEventApi.searchEvents()
                val items = response.collection.items

                val events = items.mapNotNull { item ->
                    val data = item.data.firstOrNull()
                    val image = item.links.firstOrNull()?.href

                    if (data != null && image != null) {
                        NasaEvent(
                            title = data.title,
                            date = data.date_created.substring(0, 10),
                            description = data.description,
                            imageUrl = image
                        )
                    } else null
                }

                _events.value = events
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
