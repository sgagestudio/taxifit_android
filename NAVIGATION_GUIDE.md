# Navigation Guide - Taxi ERP Mock App

## Entry Flow
1. `login` (`LoginScreen`)
2. `personal_data` (`PersonalDataScreen`)
3. `role_selection` (`RoleSelectionScreen`)
4. If role is `OWNER` -> `vehicle_data` (`VehicleDataScreen`) -> `owner_home`
5. If role is `DRIVER` -> `driver_home`

Main graph implementation:
- `app/src/main/java/com/mm/taxifit/ui/navigation/TaxiNavHost.kt`

## Driver Navigation
Bottom bar is shown automatically when route belongs to Driver graph:
- `driver_home` -> `HomeDriverScreen`
- `driver_history` -> `HistoryScreen`
- `driver_goals` -> `GoalsScreen`
- `driver_vehicle_status` -> `VehicleStatusScreen`

## Owner Navigation
Bottom bar is shown automatically when route belongs to Owner graph:
- `owner_home` -> `HomeOwnerScreen`
- `owner_fleet` -> `FleetManagementScreen`
- `owner_financials` -> `FinancialsScreen`
- `owner_reports` -> `ReportsScreen`

## ViewModel Mapping
- `LoginScreen` -> `LoginViewModel`
- `PersonalDataScreen` -> `PersonalDataViewModel`
- `RoleSelectionScreen` -> `RoleSelectionViewModel`
- `VehicleDataScreen` -> `VehicleDataViewModel`
- `HomeDriverScreen` -> `HomeDriverViewModel`
- `HistoryScreen` -> `HistoryViewModel`
- `GoalsScreen` -> `GoalsViewModel`
- `VehicleStatusScreen` -> `VehicleStatusViewModel`
- `HomeOwnerScreen` -> `HomeOwnerViewModel`
- `FleetManagementScreen` -> `FleetManagementViewModel`
- `FinancialsScreen` -> `FinancialsViewModel`
- `ReportsScreen` -> `ReportsViewModel`

All ViewModels:
- `app/src/main/java/com/mm/taxifit/ui/viewmodels`

## Mock Data and Repository Layer
Repository interfaces:
- `app/src/main/java/com/mm/taxifit/data/repository/Repositories.kt`

Mock models:
- `app/src/main/java/com/mm/taxifit/data/repository/MockDataModels.kt`

Mock implementations:
- `app/src/main/java/com/mm/taxifit/data/repository/MockRepositories.kt`

Repository provider used by ViewModels:
- `app/src/main/java/com/mm/taxifit/data/repository/RepositoryProvider.kt`

Role and route definitions:
- `app/src/main/java/com/mm/taxifit/data/repository/AppUserRole.kt`
- `app/src/main/java/com/mm/taxifit/ui/navigation/AppRoute.kt`
