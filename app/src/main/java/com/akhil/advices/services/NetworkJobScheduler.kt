package com.akhil.advices.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.akhil.advices.dao.MainResponse
import com.akhil.advices.repositories.AdviceRepository
import com.akhil.advices.util.Constants
import com.akhil.advices.util.NotificationUtil
import com.akhil.advices.util.Utilities
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class NetworkJobScheduler : JobService() {

    @Inject
    lateinit var utilities: Utilities
    @Inject
    lateinit var adviceRepository: AdviceRepository
    @Inject
    lateinit var notificationUtil: NotificationUtil

    override fun onStartJob(params: JobParameters): Boolean {
        doBackgroundWork(params)
        return true
    }

    private fun doBackgroundWork(params: JobParameters) {
        val theme = utilities.getRandomTheme()
        CoroutineScope(Dispatchers.Main).launch {
            val job = withTimeoutOrNull(Constants.NETWORK_TIMEOUT) {
                adviceRepository.getAdvice()
                    .onEach { dataState ->
                        notificationUtil.showNotification(dataState.slip.advice,theme.accent, theme.color)
                        utilities.setNextAlarm()
                        jobFinished(params, false)
                    }
                    .launchIn(this)
            }

            if (job == null) {
                jobFinished(params, true)
            }
        }
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true
    }

}