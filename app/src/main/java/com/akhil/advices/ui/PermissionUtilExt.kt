package com.akhil.advices.ui

import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akhil.advices.R
import com.akhil.advices.ui.fragments.AdviceFragment
import timber.log.Timber

private var onResults: ((Boolean) -> Unit)? = null
private var requestPermission: ActivityResultLauncher<String>? = null

internal fun AdviceFragment.checkPermission(
    permission: String,
    onResult: (Boolean) -> Unit
) {
    onResults = onResult

    if (ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        Timber.e("Camera permission true")
        onResults?.invoke(true)
    } else {
        Timber.e("Camera permission false")
        checkPermissionRational(permission)
    }
}

private fun AdviceFragment.checkPermissionRational(
    permission: String
) {

    if (ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            permission
        )
    ) {
        AlertDialog.Builder(requireActivity(), R.style.TimePickerDialogStyle)
            .setTitle("Permission needed")
            .setMessage("Please allow permission to show notification")
            .setPositiveButton("OK") { _, _ ->
                requestPermission?.launch(permission)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create().show()
    } else {
        requestPermission?.launch(permission)
    }
}

internal fun AdviceFragment.initPermission() {
    requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onResults?.invoke(isGranted)
    }
}