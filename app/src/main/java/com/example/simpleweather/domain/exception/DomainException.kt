package com.example.simpleweather.domain.exception

import androidx.annotation.StringRes

sealed class DomainException(
    message: String? = null,
    cause: Throwable? = null,
    @StringRes val messageResId: Int? = null
) : Exception(message, cause)