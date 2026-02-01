package com.mm.taxifit.domain.model

data class AuthSession(
    val userId: String,
    val role: UserRole,
    val tokens: AuthTokens
)
