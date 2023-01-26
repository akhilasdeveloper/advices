package com.akhil.advices.di

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.hardware.input.InputManager
import android.text.format.DateFormat
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import com.akhil.advices.api.AdvicesMainService
import com.akhil.advices.receivers.AlertReceiver
import com.akhil.advices.services.NetworkJobScheduler
import com.akhil.advices.util.Constants
import com.akhil.advices.util.Constants.JOB_LATENCY
import com.akhil.advices.util.Constants.SHARED_PREFERENCES_NAME
import com.akhil.advices.util.NotificationUtil
import com.akhil.advices.util.Utilities
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Inet4Address

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(Constants.URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideMainService(retrofit: Retrofit.Builder): AdvicesMainService {
        return retrofit
            .build()
            .create(AdvicesMainService::class.java)
    }

    @Singleton
    @Provides
    fun provideUtilities(
        sharedPref: SharedPreferences,
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent,
        alarmManager: AlarmManager
    ): Utilities {
        return Utilities(sharedPref, context, pendingIntent, alarmManager)
    }

    @Singleton
    @Provides
    fun providesInputManager(@ApplicationContext context: Context): InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    @Singleton
    @Provides
    fun provideAlarmIntent(@ApplicationContext context: Context): Intent =
        Intent(context, AlertReceiver::class.java).apply {
            action = Constants.ALARM_ACTION_STRING
        }

    @Singleton
    @Provides
    fun provideAlarm(@ApplicationContext context: Context): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Singleton
    @Provides
    fun provideAlarmPendingIntent(
        @ApplicationContext context: Context,
        intent: Intent
    ): PendingIntent = PendingIntent.getBroadcast(
        context,
        Constants.ALARM_REQUEST_ID,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )


    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext app: Context
    ): SharedPreferences = app.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideRotateAnimation() = RotateAnimation(
        0f,
        360f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    ).apply {
        duration = Constants.ANIMATION_ROTATE_DURATION
        fillAfter = true
        interpolator = LinearInterpolator()
    }

    @Singleton
    @Provides
    fun provideJobComponent(
        @ApplicationContext app: Context
    ): ComponentName = ComponentName(app, NetworkJobScheduler::class.java)

    @Singleton
    @Provides
    fun provideJobInfo(
        componentName: ComponentName
    ): JobInfo = JobInfo.Builder(Constants.JOB_ID, componentName)
        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        .setMinimumLatency(JOB_LATENCY)
        .setBackoffCriteria(JOB_LATENCY, JobInfo.BACKOFF_POLICY_LINEAR)
        .setPersisted(true)
        .build()

    @Singleton
    @Provides
    fun provideJobScheduler(
        @ApplicationContext app: Context
    ): JobScheduler = app.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

    @Singleton
    @Provides
    fun provideNotification(
        @ApplicationContext app: Context
    ): NotificationUtil = NotificationUtil(app)
}
