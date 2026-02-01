package com.mm.taxifit.domain.model

sealed class OnboardingStep {
    data object Profile : OnboardingStep()
    data object Vehicle : OnboardingStep()
    data object None : OnboardingStep()
}
