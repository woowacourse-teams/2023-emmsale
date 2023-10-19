package com.emmsale.presentation.common.extension

import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.UpdateAvailability

fun AppUpdateInfo.isUpdateNeeded(): Boolean {
    return updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
}

fun Task<AppUpdateInfo>.addListener(
    onSuccess: (updateInfo: AppUpdateInfo) -> Unit = {},
    onFailed: (error: Exception) -> Unit = {},
) {
    addOnSuccessListener { appUpdateInfo -> onSuccess(appUpdateInfo) }
    addOnFailureListener { error -> onFailed(error) }
}
