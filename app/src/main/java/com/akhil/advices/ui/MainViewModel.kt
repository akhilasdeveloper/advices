package com.akhil.advices.ui

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akhil.advices.dao.MainResponse
import com.akhil.advices.repositories.AdviceRepository
import com.akhil.advices.util.Constants.NETWORK_NOT_AVAILABLE_MESSAGE
import com.akhil.advices.util.Constants.NETWORK_SUCCESS
import com.akhil.advices.util.Constants.NETWORK_TIMEOUT
import com.akhil.advices.util.Constants.NETWORK_TIMEOUT_MESSAGE
import com.akhil.advices.util.Utilities
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val adviceRepository: AdviceRepository,
    private val utilities: Utilities
) : ViewModel() {

    private val _dataState: MutableLiveData<MainResponse> = MutableLiveData()

    val dataState: LiveData<MainResponse>
        get() = _dataState

    fun getData() {

        if (!utilities.isConnectedToTheInternet()) {
            val mainResponse = MainResponse(
                null,
                NETWORK_NOT_AVAILABLE_MESSAGE
            )
            _dataState.value = mainResponse
            return
        }


        viewModelScope.launch {
            val job = withTimeoutOrNull(NETWORK_TIMEOUT) {
                adviceRepository.getAdvice()
                    .onEach { dataState ->
                        val mainResponse: MainResponse = MainResponse(
                            dataState,
                            NETWORK_SUCCESS
                        )
                        _dataState.value = mainResponse
                    }
                    .launchIn(this)
            }

            if (job == null) {
                val mainResponse: MainResponse = MainResponse(
                    null,
                    NETWORK_TIMEOUT_MESSAGE
                )
                _dataState.value = mainResponse
            }
        }
    }
}