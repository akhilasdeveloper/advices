package com.akhil.advices.receivers

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.akhil.advices.R
import com.akhil.advices.di.HiltDaggerBroadcastReceiver
import com.akhil.advices.util.Constants
import com.akhil.advices.util.NotificationUtil
import com.akhil.advices.util.Utilities
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlertReceiver: HiltDaggerBroadcastReceiver() {

    @Inject
    lateinit var utilities : Utilities
    @Inject
    lateinit var jobInfo: JobInfo
    @Inject
    lateinit var jobScheduler: JobScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context,intent)
        context?.let {
            intent?.let { intent->
                when(intent.action){
                    Intent.ACTION_BOOT_COMPLETED ->{
                        val alarmTime = utilities.getAlarm()
                        val currTime = System.currentTimeMillis()
                        if (alarmTime < currTime) {
                            scheduleJob()
                        }else{
                            utilities.setAlarm(alarmTime,context)
                        }
                    }
                    Constants.ALARM_ACTION_STRING ->{
                        scheduleJob()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun scheduleJob() {
        val resultCode: Int = jobScheduler.schedule(jobInfo)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Timber.d("Job scheduled")
        } else {
            Timber.d("Job scheduling failed")
        }
    }
}