package com.example.constelaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.constelaciones.data.remote.LibreTranslateClient
import com.example.constelaciones.data.remote.MyMemoryTranslateClient
import com.example.constelaciones.data.remote.TranslateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventDetailViewModel(private val originalDescription: String) : ViewModel() {

    private val _translated = MutableStateFlow<String?>(null)
    val translated = _translated.asStateFlow()

    private val _isSpanish = MutableStateFlow(true)
    val isSpanish = _isSpanish.asStateFlow()

    init {
        translateDescription()
    }

    fun toggleLanguage() {
        _isSpanish.value = !_isSpanish.value
    }

    private fun translateDescription() {
        viewModelScope.launch {
            try {
                val memoryResponse = MyMemoryTranslateClient.api.translate(
                    text = originalDescription,
                    langPair = "en|es",
                    email = "domingogaby8@gmail.com",
                    apiKey = "4baef49ce12c2609e0a2"
                )

                if (memoryResponse.responseStatus == 200) {
                    _translated.value = memoryResponse.responseData.translatedText
                    return@launch
                }

            } catch (_: Exception) {}

            try {
                val libreResponse = LibreTranslateClient.api.translate(
                    TranslateRequest(q = originalDescription)
                )
                _translated.value = libreResponse.translatedText
            } catch (_: Exception) {
                _translated.value = originalDescription
            }
        }
    }

    class Factory(private val original: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EventDetailViewModel(original) as T
        }
    }
}
