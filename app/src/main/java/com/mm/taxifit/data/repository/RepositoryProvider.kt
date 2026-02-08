package com.mm.taxifit.data.repository

object RepositoryProvider {
    val authRepository: AuthRepository = MockAuthRepository()
    val onboardingRepository: OnboardingRepository = MockOnboardingRepository()
    val driverRepository: DriverRepository = MockDriverRepository()
    val ownerRepository: OwnerRepository = MockOwnerRepository()
}
