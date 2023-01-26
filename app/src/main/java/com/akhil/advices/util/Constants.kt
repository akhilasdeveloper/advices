package com.akhil.advices.util

object Constants {
    const val CHANNEL_ID : String = "com.akhil.advices.channel1"
    const val URL: String = "https://api.adviceslip.com/"
    const val ANIMATION_DURATION = 1000
    const val ANIMATION_ROTATE_DURATION = 1000L
    const val COLOR_DIFFERENCE = 100
    const val ANIMATION_ENTER_FADE_DURATION = 500L
    const val ANIMATION_EXIT_FADE_DURATION = 500L
    const val SHARED_PREFERENCES_NAME = "sharedPref"
    const val KEY_IS_SCHEDULED = "KEY_IS_SCHEDULED"
    const val KEY_ALARM_TIME = "KEY_ALARM_TIME"
    const val KEY_SAVED_FONT = "KEY_SAVED_FONT"
    const val FONT_SIZE = 32f
    const val ALARM_ACTION_STRING = "ADVICES_ALARM_ACTION_STRING"
    const val JOB_ID = 123
    const val ALARM_REQUEST_ID = 100
    const val NOTIFICATION_ID = 190
    const val DAY_MILLIS = 1000 * 60 * 60 * 24
    const val JOB_LATENCY = (1000 * 60 * 1).toLong()
    const val INTENT_KEY_MESSAGE = "INTENT_KEY_MESSAGE"
    const val INTENT_KEY_COLOR1 = "INTENT_KEY_COLOR1"

    const val NETWORK_TIMEOUT = 5000L
    const val NETWORK_SUCCESS = "Success"
    const val NETWORK_TIMEOUT_MESSAGE = "Network timed out"
    const val NETWORK_NOT_AVAILABLE_MESSAGE = "Check network settings"
}