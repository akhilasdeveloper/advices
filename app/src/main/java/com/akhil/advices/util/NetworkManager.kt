package com.akhil.advices.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.akhil.advices.util.Constants.ERROR_UNKNOWN
import com.akhil.advices.util.Constants.NETWORK_TIMEOUT
import com.akhil.advices.util.Constants.TESTING_NETWORK_DELAY
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


class NetworkManager() {

    /*protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        doNetworkRequest()
    }

    fun setJob(){
        initNewJob()
    }


    private fun doNetworkRequest() {
        coroutineScope.launch {

            delay(TESTING_NETWORK_DELAY)

            withContext(Main) {

                val apiResponse = createCall()
                result.addSource(apiResponse) { response ->
                    result.removeSource(apiResponse)

                    coroutineScope.launch {
                        handleNetworkCall(response)
                    }
                }
            }
        }

        GlobalScope.launch(IO) {
            delay(NETWORK_TIMEOUT)

            if (!job.isCompleted) {
                job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
            }
        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse -> {
                onErrorReturn(response.errorMessage, true, false)
            }
            is ApiEmptyResponse -> {
                onErrorReturn("HTTP 204. Returned nothing.", true, false)
            }
        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {

        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()
        if (msg == null) {
            msg = ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(msg)) {
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast) {
            responseType = ResponseType.Toast()
        }
        if (useDialog) {
            responseType = ResponseType.Dialog()
        }

        onCompleteJob(DataState.error(Response(msg, responseType)))
    }

    fun onSuccessReturn(successMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {

        var msg = successMessage
        var responseType: ResponseType = ResponseType.None()
        if (msg == null) {
            msg = GENERAL_SUCCESS
        }
        if (shouldUseToast) {
            responseType = ResponseType.Toast()
        }
        if (shouldUseDialog) {
            responseType = ResponseType.Dialog()
        }

        onCompleteJob(DataState.success(Response(msg, responseType)))
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        job = Job()
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {

                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) {
                        cause?.let {
                            onErrorReturn(it.message, false, true)
                        } ?: onErrorReturn(ERROR_UNKNOWN, false, true)
                    }
                }

            })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }*/

}