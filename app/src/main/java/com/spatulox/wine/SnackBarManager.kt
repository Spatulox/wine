package com.spatulox.wine

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class SnackbarManager {
    private var _messageChannel = Channel<String>(Channel.CONFLATED)

    val messageFlow: Flow<String> = _messageChannel.receiveAsFlow()

    suspend fun send(message: String) {
        _messageChannel.send(message)
    }

    companion object {
        val INSTANCE = SnackbarManager()
    }
}

suspend fun SnackbarManager.Companion.send(message: String) {
    INSTANCE.send(message)
}