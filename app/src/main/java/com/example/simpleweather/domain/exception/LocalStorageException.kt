package com.example.simpleweather.domain.exception

import androidx.annotation.StringRes
import com.example.simpleweather.R

class LocalStorageException(
    message: String? = "Fail to read/write data to local storage",
    cause: Throwable? = null,
    @StringRes messageResId: Int? = R.string.local_storage_exception
) : DomainException(message, cause, messageResId)