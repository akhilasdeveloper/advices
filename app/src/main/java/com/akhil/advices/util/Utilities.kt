package com.akhil.advices.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.akhil.advices.R
import com.akhil.advices.dao.DrawableAndText
import com.akhil.advices.dao.Theme
import com.akhil.advices.dao.TransitionAndText
import com.akhil.advices.util.Constants.COLOR_DIFFERENCE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


class Utilities @Inject constructor(
    var sharedPref: SharedPreferences,
    var context: Context,
    var pendingIntent: PendingIntent,
    var alarmManager: AlarmManager
) {

    var typefaces = ArrayList<Typeface>()
    var fontPaths = arrayListOf<Int>(
        R.font.roboto_regular,
        R.font.anton_regular,
        R.font.caveat_regular,
        R.font.indieflower_regular,
        R.font.lobster_regular,
        R.font.pacifico_regular,
        R.font.peddana_regular,
        R.font.sansitaswashed_variablefont_wght,
        R.font.shadowsintolight_regular,
        R.font.yanonekaffeesatz_variablefont_wght
    )

    init {

        for (name in fontPaths) {
            ResourcesCompat.getFont(context, name)?.let {
                typefaces.add(it)
            }
        }
    }

    fun getFont(): Typeface {
        return typefaces[getSavedFont()]
    }


    fun getNextFont(): Typeface {
        var currFont = getSavedFont()
        currFont++
        if (currFont >= (typefaces.size))
            currFont = 0
        setSavedFont(currFont)
        return typefaces[currFont]
    }

    private fun getSavedFont(): Int {
        return sharedPref.getInt(Constants.KEY_SAVED_FONT, 0)
    }

    private fun setSavedFont(font: Int) {
        sharedPref.edit()
            .putInt(Constants.KEY_SAVED_FONT, font)
            .apply()
    }

    fun getInitTransition(): TransitionAndText {
        val drawableArray = ArrayList<Drawable>()
        val theme1 = getBlackTheme()
        val theme2 = getBlackTheme()
        drawableArray.add(theme1.drawable)
        drawableArray.add(theme2.drawable)
        return TransitionAndText(
            TransitionDrawable(drawableArray.toTypedArray()),
            theme2.accent,
            theme2.accentSecond,
            theme2.color
        )
    }

    fun getNextTransition(transitionDrawable: TransitionDrawable): TransitionAndText {
        val drawableArray = ArrayList<Drawable>()
        drawableArray.add(transitionDrawable.getDrawable(1))
        val theme2 = getRandomTheme()
        drawableArray.add(theme2.drawable)
        return TransitionAndText(
            TransitionDrawable(drawableArray.toTypedArray()),
            theme2.accent,
            theme2.accentSecond,
            theme2.color
        )
    }

    fun getTransitionByColor(
        transitionDrawable: TransitionDrawable,
        color: Int
    ): TransitionAndText {
        val drawableArray = ArrayList<Drawable>()
        drawableArray.add(transitionDrawable.getDrawable(1))
        val theme2 = getThemeByColor(color)
        drawableArray.add(theme2.drawable)
        return TransitionAndText(
            TransitionDrawable(drawableArray.toTypedArray()),
            theme2.accent,
            theme2.accentSecond,
            theme2.color
        )
    }

    private fun getDrawable(theme: Theme): GradientDrawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.orientation = GradientDrawable.Orientation.TR_BL
        shape.colors = arrayOf(theme.start_color, theme.end_color).toIntArray()
        return shape
    }

    private fun getBlackTheme(): DrawableAndText {
        val col1 = Color.rgb(255, 253, 120)
        val col2 = Color.rgb(255, 159, 1)
        val col3 = Color.rgb(0, 0, 0)

        val theme = Theme(col1, col2, col3)
        return DrawableAndText(
            getDrawable(theme),
            col2,
            col1,
            theme.text_color
        )
    }

    fun getRandomTheme(): DrawableAndText {
        val col1R = (0 until 255).random()
        val col1G = (0 until 255).random()
        val col1B = (0 until 255).random()
        val col1 = Color.rgb(col1R, col1G, col1B)

        val col2R = getLowValue(col1R, COLOR_DIFFERENCE)
        val col2G = getLowValue(col1G, COLOR_DIFFERENCE)
        val col2B = getLowValue(col1B, COLOR_DIFFERENCE)
        val col2 = Color.rgb(col2R, col2G, col2B)

        var col3R = col1R
        var col3G = col1G
        var col3B = col1B

        var col4R = col1R
        var col4G = col1G
        var col4B = col1B

        val luma1 = 0.2126 * col1R + 0.7152 * col1G + 0.0722 * col1B
        val luma2 = 0.2126 * col2R + 0.7152 * col2G + 0.0722 * col2B

        var colA = Color.rgb(255, 255, 255)

        if ((luma1 + luma2) / 2 > 100)
            colA = Color.rgb(0, 0, 0)

        if (luma1 < 150) {
            val lumaT = 150 - luma1
            val fact = (lumaT / 3).toInt()
            col3R = getHighValue(col3R, fact)
            col3G = getHighValue(col3G, fact)
            col3B = getHighValue(col3B, fact)
        }

        val luma3 = 0.2126 * col3R + 0.7152 * col3G + 0.0722 * col3B

        val lumaU = luma3 + 150
        val factU = (lumaU / 3).toInt()
        col4R = getHighValue(col4R, factU)
        col4G = getHighValue(col4G, factU)
        col4B = getHighValue(col4B, factU)

        val theme = Theme(col1, col2, colA)

        return DrawableAndText(
            getDrawable(theme),
            Color.rgb(col3R, col3G, col3B),
            Color.rgb(col4R, col4G, col4B),
            theme.text_color
        )
    }

    private fun getThemeByColor(color: Int): DrawableAndText {
        val col1R = Color.red(color)
        val col1G = Color.green(color)
        val col1B = Color.blue(color)
        val col1 = Color.rgb(col1R, col1G, col1B)

        val col2R = getLowValue(col1R, COLOR_DIFFERENCE)
        val col2G = getLowValue(col1G, COLOR_DIFFERENCE)
        val col2B = getLowValue(col1B, COLOR_DIFFERENCE)
        val col2 = Color.rgb(col2R, col2G, col2B)

        var col3R = col1R
        var col3G = col1G
        var col3B = col1B

        var col4R = col1R
        var col4G = col1G
        var col4B = col1B

        val luma1 = 0.2126 * col1R + 0.7152 * col1G + 0.0722 * col1B
        val luma2 = 0.2126 * col2R + 0.7152 * col2G + 0.0722 * col2B

        var colA = Color.rgb(255, 255, 255)

        if ((luma1 + luma2) / 2 > 100)
            colA = Color.rgb(0, 0, 0)

        if (luma1 < 150) {
            val lumaT = 150 - luma1
            val fact = (lumaT / 3).toInt()
            col3R = getHighValue(col3R, fact)
            col3G = getHighValue(col3G, fact)
            col3B = getHighValue(col3B, fact)
        }

        val luma3 = 0.2126 * col3R + 0.7152 * col3G + 0.0722 * col3B

        val lumaU = luma3 + 150
        val factU = (lumaU / 3).toInt()
        col4R = getHighValue(col4R, factU)
        col4G = getHighValue(col4G, factU)
        col4B = getHighValue(col4B, factU)

        val theme = Theme(col1, col2, colA)

        return DrawableAndText(
            getDrawable(theme),
            Color.rgb(col3R, col3G, col3B),
            Color.rgb(col4R, col4G, col4B),
            theme.text_color
        )
    }

    private fun getLowValue(value: Int, diff: Int): Int {
        var newValue = value - diff
        if (newValue < 0)
            newValue = 0
        return newValue
    }

    private fun getHighValue(value: Int, diff: Int): Int {
        var newValue = value + diff
        if (newValue > 254)
            newValue = 254
        return newValue
    }

    fun getFactColor(color1: Int, color2: Int, fact: Float): Int {

        val nFact = 1 - fact

        var c1R = Color.red(color1)
        var c1G = Color.green(color1)
        var c1B = Color.blue(color1)

        var c2R = Color.red(color2)
        var c2G = Color.green(color2)
        var c2B = Color.blue(color2)

        c1R = (c1R * fact).toInt()
        c1B = (c1B * fact).toInt()
        c1G = (c1G * fact).toInt()

        c2R = (c2R * nFact).toInt()
        c2B = (c2B * nFact).toInt()
        c2G = (c2G * nFact).toInt()

        return Color.rgb(getHighValue(c1R, c2R), getHighValue(c1G, c2G), getHighValue(c1B, c2B))
    }


    suspend fun toImageURI(bitmap: Bitmap?): Uri? {
        bitmap?.let {
            var file: File? = null
            var fos1: FileOutputStream? = null
            var imageUri: Uri? = null
            try {
                val folder = File(
                    context.cacheDir.toString() + File.separator + "Advices Temp Files"
                )
                if (!folder.exists()) {
                    folder.mkdir()
                }
                val filename = "advices.jpg"
                file = File(folder.path, filename)
                withContext(Dispatchers.IO) {
                    fos1 = FileOutputStream(file)
                }

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos1)
                imageUri = FileProvider.getUriForFile(
                    context.applicationContext,
                    context.applicationContext.packageName.toString() + ".provider",
                    file
                )
            } catch (ex: java.lang.Exception) {
            } finally {
                try {
                    withContext(Dispatchers.IO) {
                        fos1?.close()
                    }
                } catch (e: IOException) {
                    Timber.d("Unable to close connection Utilities toImageURI : ${e.toString()}")
                }
            }
            return imageUri
        }
        return null
    }

    fun setNextAlarm() {
        var alarmTime = getAlarm()
        alarmTime += Constants.DAY_MILLIS
        setAlarmTime(alarmTime)
        setAlarm(alarmTime, context)
    }

    fun setAlarm(millis: Long, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent)
        else
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent)
    }

    fun cancelAlarm() {
        alarmManager.cancel(pendingIntent)
    }

    fun setScheduled(checked: Boolean) {
        sharedPref.edit()
            .putBoolean(Constants.KEY_IS_SCHEDULED, checked)
            .apply()
    }

    fun setAlarmTime(millis: Long) {
        sharedPref.edit()
            .putLong(Constants.KEY_ALARM_TIME, millis)
            .apply()
    }

    fun getAlarm(): Long {
        return sharedPref.getLong(Constants.KEY_ALARM_TIME, 0)
    }

    fun isScheduled(): Boolean {
        return sharedPref.getBoolean(Constants.KEY_IS_SCHEDULED, false)
    }

    fun isConnectedToTheInternet(): Boolean {
        try {
            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }
            return result
        } catch (e: Exception) {
            Timber.d("isConnectedToTheInternet: ${e.message}")
        }
        return false
    }

}
