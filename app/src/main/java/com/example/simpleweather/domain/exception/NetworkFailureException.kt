package com.example.simpleweather.domain.exception

import androidx.annotation.StringRes

class NetworkFailureException(
    message: String? = "Error while connecting to the internet",
    cause: Throwable? = null,
    @StringRes messageResId: Int? = null
) : DomainException(message, cause, messageResId)