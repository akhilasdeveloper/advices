package com.akhil.advices.di

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

abstract class HiltDaggerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {}
}