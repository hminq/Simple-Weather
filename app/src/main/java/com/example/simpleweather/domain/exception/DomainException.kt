package com.example.simpleweather.domain.exception

import androidx.annotation.StringRes
import com.example.simpleweather.R

sealed class DomainException(
    message: String? = null,
    cause: Throwable? = null,
    @StringRes val messageResId: Int? = R.string.general_error
) : Exception(message, cause)