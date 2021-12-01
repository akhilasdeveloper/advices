package com.akhil.advices.ui.fragments

import android.app.TimePickerDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TimePicker
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.akhil.advices.R
import com.akhil.advices.dao.TransitionAndText
import com.akhil.advices.ui.MainViewModel
import com.akhil.advices.ui.fragments.extensions.*
import com.akhil.advices.util.Constants
import com.akhil.advices.util.Constants.FONT_SIZE
import com.akhil.advices.util.Constants.INTENT_KEY_COLOR1
import com.akhil.advices.util.Constants.INTENT_KEY_MESSAGE
import com.akhil.advices.util.Constants.NETWORK_NOT_AVAILABLE_MESSAGE
import com.akhil.advices.util.Constants.NETWORK_SUCCESS
import com.akhil.advices.util.Constants.NETWORK_TIMEOUT_MESSAGE
import com.akhil.advices.util.NotificationUtil
import com.akhil.advices.util.Utilities
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_advice.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AdviceFragment : Fragment(R.layout.fragment_advice), TimePickerDialog.OnTimeSetListener {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    internal lateinit var transit: TransitionAndText
    internal lateinit var popupMenu: PopupMenu

    @Inject
    lateinit var rotate: RotateAnimation
    lateinit var fadeIn: AlphaAnimation
    lateinit var fadeOut: AlphaAnimation
    internal var isLoading = false
    internal var isMenuOpened = false
    internal var isAnimationLoading = MutableLiveData(false)
    private var isShareImageClicked = false
    private var message = ""
    private lateinit var timePickerDialog2: TimePickerDialog

    @Inject
    lateinit var utilities: Utilities

    @Inject
    lateinit var notificationUtil: NotificationUtil
    private var isTextView = true

    @Inject
    lateinit var imm: InputMethodManager

    @Inject
    lateinit var jobInfo: JobInfo

    @Inject
    lateinit var jobScheduler: JobScheduler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        timePickerDialog2 = TimePickerDialog(
            requireContext(),
            R.style.TimePickerDialogStyle,
            this,
            0,
            0,
            DateFormat.is24HourFormat(requireContext())
        )

        setFont()
        setClickListners()
        setBottomSheet()
        init()
        setPopuMenu()
        initAnimations()
        subscribeObservers()
        initTransition()

        val intent: Intent = requireActivity().intent
        val msg = intent.getStringExtra(INTENT_KEY_MESSAGE)
        val col1 = intent.getStringExtra(INTENT_KEY_COLOR1)

        if (msg != null && col1 != null) {
            message = msg
            setData()
            initNextTransitionByColor(col1.toInt())
            main_activity_bg.background = transit.drawable.getDrawable(1)
            setTint()
        } else {
            getData()
        }

        setSwitchTint()
        setTimeButtonBackground()

    }

    private fun init() {
        val peekHeight = bottomSheetBehavior.peekHeight
        val bottomSheetParams = bottom_sheet.layoutParams as CoordinatorLayout.LayoutParams
        val bottomSheetHeight = bottomSheetParams.height
        ViewCompat.setOnApplyWindowInsetsListener(bottom_sheet) { _, insets ->
            val systemWindows =
                insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            val barMenuParams = bar_menus.layoutParams as LinearLayout.LayoutParams
            val barSettingsParams = bar_settings.layoutParams as LinearLayout.LayoutParams
            barMenuParams.bottomMargin = systemWindows.bottom + 50
            barSettingsParams.bottomMargin = systemWindows.bottom + 50
            bottomSheetParams.height = (systemWindows.bottom * 2) + bottomSheetHeight + 100
            bottomSheetBehavior.peekHeight = systemWindows.bottom + peekHeight + 50
            return@setOnApplyWindowInsetsListener insets
        }
    }

    private fun setFont() {
        val font = utilities.getFont()
        text_view.typeface = font
        edit_text.typeface = font
        text_view.textSize = FONT_SIZE
        edit_text.textSize = FONT_SIZE
    }

    private fun setNextFont() {
        val font = utilities.getNextFont()
        text_view.typeface = font
        edit_text.typeface = font
        text_view.textSize = FONT_SIZE
        edit_text.textSize = FONT_SIZE
    }

    private fun setPopuMenu() {
        popupMenu = PopupMenu(requireContext(), share)
        popupMenu.menuInflater.inflate(R.menu.share_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { p0 ->
            var bool = false
            p0?.let { menuItem ->
                when (menuItem.itemId) {
                    R.id.asText -> {
                        if (message.isNotEmpty())
                            shareAsText()
                        bool = true
                    }
                    R.id.asImage -> {
                        if (message.isNotEmpty()) {
                            isAnimationLoading.value?.let { isAnimationLoading ->
                                if (isAnimationLoading)
                                    isShareImageClicked = true
                                else
                                    shareAsImage()
                            }
                        }
                        bool = true
                    }
                }
            }

            bool
        }

        popupMenu.setOnDismissListener {
            isMenuOpened = false
            setShareTint()
        }
    }

    private fun setBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        bottomSheetBehavior.isGestureInsetBottomIgnored = true
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                setAlarmTint(slideOffset)
                setSwitchTint(slideOffset)
                setTimeButtonBackground(slideOffset)
            }

        })
    }

    private fun setClickListners() {
        alarm.setOnClickListener {
            if (isNavExpanded()) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        refresh.setOnClickListener {
            if (!isLoading) {
                getData()
            }
        }

        share.setOnClickListener {
            isMenuOpened = true
            setShareTint()
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup.get(popupMenu)
                menu.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                popupMenu.show()
            }
        }

        switchButton.isChecked = utilities.isScheduled()
        setTimeText()
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            utilities.setScheduled(isChecked)
            if (isChecked) {
                utilities.setAlarmTime(utilities.getAlarm())
            } else {
                utilities.cancelAlarm()
            }
            setSwitchTint()
            setTimeButtonBackground()
        }

        time.setOnClickListener {
            if (utilities.isScheduled())
                openTimePicker()
        }

        font.setOnClickListener {
            setNextFont()
        }

        constraintView.setOnClickListener {
            toggleEditMode()
        }

        text_view.setOnClickListener {
            showEditMode()
        }

        background.setOnClickListener {
            initNextTransition()
            main_activity_bg.background = transit.drawable.getDrawable(1)
            setTint()
        }

    }

    private fun toggleEditMode() {
        if (isTextView) {
            showEditMode()
        } else {
            hideEditMode()
        }
    }

    private fun hideEditMode() {
        if (!isTextView) {
            text_view.visibility = View.VISIBLE
            edit_text.visibility = View.GONE
            isTextView = true
            imm.hideSoftInputFromWindow(edit_text.windowToken, 0)
            saveData()
        }
    }

    private fun saveData() {
        message = edit_text.text.toString()
        text_view.text = message
    }

    private fun showEditMode() {
        if (isTextView) {
            text_view.visibility = View.GONE
            edit_text.visibility = View.VISIBLE
            isTextView = false
            edit_text.requestFocus()
            imm.showSoftInput(edit_text, 0)
        }
    }

    private fun setTimeText() {
        var mills = utilities.getAlarm()
        val c = Calendar.getInstance()
        if (mills == 0L) {
            mills = c.timeInMillis
            utilities.setAlarmTime(mills)
        }
        c.timeInMillis = mills
        time.text = formatMillis(mills)
    }

    private fun openTimePicker() {
        val now = Calendar.getInstance()
        timePickerDialog2.updateTime(now[Calendar.HOUR_OF_DAY], now[Calendar.MINUTE])
        timePickerDialog2.show()
    }


    internal fun isNavExpanded(): Boolean {
        return bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
    }

    private fun formatMillis(millis: Long): String {
        var pattern = "hh:mm a"
        if (DateFormat.is24HourFormat(requireContext())) {
            pattern = "HH:mm"
        }
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(Date(millis))
    }

    private fun shareAsImage() {
        isShareImageClicked = false
        hideOtherFields()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val bitmap = main_activity_bg.drawToBitmap(Bitmap.Config.ARGB_8888)
            val imageUri: Uri? = utilities.toImageURI(bitmap)
            withContext(Dispatchers.Main) {
                showOtherFields()
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    type = "image/jpeg"
                }
                startActivity(Intent.createChooser(shareIntent, "Share image via"))
            }
        }
    }

    private fun hideOtherFields() {
        bottom_sheet.visibility = View.GONE
    }

    private fun showOtherFields() {
        bottom_sheet.visibility = View.VISIBLE
    }

    internal fun setData() {
        setTint()
        text_view.text = message
        edit_text.setText(message)
        if (lanuch_icon.visibility == View.VISIBLE)
            lanuch_icon.visibility = View.GONE
        startFadeInAnimation()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { response ->
            isLoading = false
            initNextTransition()
            transit.drawable.startTransition(Constants.ANIMATION_DURATION)

            when (response.networkResponse) {
                NETWORK_SUCCESS -> {
                    response.adviceResponse?.let {
                        message = it.slip.advice
                    }
                }
                NETWORK_TIMEOUT_MESSAGE -> {
                    message = NETWORK_TIMEOUT_MESSAGE
                }

                NETWORK_NOT_AVAILABLE_MESSAGE -> {
                    message = NETWORK_NOT_AVAILABLE_MESSAGE
                }
            }
        })

        isAnimationLoading.observe(viewLifecycleOwner, Observer {
            if (isShareImageClicked)
                shareAsImage()
        })
    }

    private fun shareAsText() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(sharingIntent, "Share advice via"))
    }

    private fun getData() {
        isLoading = true
        refresh.startAnimation(rotate)
        viewModel.getData()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, 0)
        var setMillis = c.timeInMillis
        val currMillis = System.currentTimeMillis()
        if (setMillis < currMillis)
            setMillis += Constants.DAY_MILLIS
        utilities.setAlarmTime(setMillis)
        setTimeText()
        utilities.setAlarm(setMillis, requireContext())
        Toast.makeText(requireContext(), "Daily advice has been scheduled.", Toast.LENGTH_SHORT)
            .show()
    }

}