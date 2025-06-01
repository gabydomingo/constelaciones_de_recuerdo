package com.example.constelaciones.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.constelaciones.ai.GeminiChatHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val apiKey = "AIzaSyCWeuwSG7GfNoNZDeRsFfOfm2x3AhpVFm0"
    private val geminiHelper = GeminiChatHelper(apiKey)

    private val _userInput = MutableStateFlow("")
    val userInput = _userInput.asStateFlow()

    private val _response = MutableStateFlow("")
    val response = _response.asStateFlow()

    fun onInputChange(newText: String) {
        _userInput.value = newText
    }

    fun sendMessage() {
        val input = _userInput.value
        if (input.isBlank()) return

        viewModelScope.launch {
            _response.value = "Pensando..."
            _response.value = geminiHelper.chatWithGemini(input)
        }
    }
}
