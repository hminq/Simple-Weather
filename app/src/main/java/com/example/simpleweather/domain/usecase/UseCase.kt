package com.example.simpleweather.domain.usecase

import kotlin.jvm.JvmSuppressWildcards

@JvmSuppressWildcards
interface UseCase<P, R> {
    suspend operator fun invoke(params: P): R
}