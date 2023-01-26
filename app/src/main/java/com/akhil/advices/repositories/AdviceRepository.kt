package com.akhil.advices.repositories

import com.akhil.advices.api.AdviceResponse
import com.akhil.advices.api.AdvicesMainService
import com.akhil.advices.api.Slip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class AdviceRepository @Inject constructor( private val advicesMainService: AdvicesMainService) {

    suspend fun getAdvice(): Flow<AdviceResponse> = flow {
        try{
            advicesMainService.getAdvice()?.let {data->
                emit(data)
                return@flow
            }
        }catch (e: Exception){
            Timber.d("Data Error: ${e.toString()}")
        }
        emit(
            AdviceResponse(
                Slip(
                    0,
                    "Response Broken.."
                )
            )
        )
    }

}