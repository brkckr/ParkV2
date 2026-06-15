# Park🅿️

Discover available ISPARK parking spots, check real-time fees, and navigate to your destination with ease.

## 📱 App Showcase
<div style="text-align: center;">
  <table>
      <td><b>1. Splash & Pre-fetch</b></td>
      <td><b>2. Map & Pager Sync</b></td>
      <td><b>3. Detail & Navigation</b></td>
    </tr>
    <tr>
      <td>
       <img width="336" height="752" alt="ss1" src="https://github.com/user-attachments/assets/21d912c9-1ef8-4732-989f-d4410feb3f5d" />
      </td>
      <td>
       <img width="336" height="752" alt="ss2" src="https://github.com/user-attachments/assets/149ab03f-b345-476d-bfdd-cbb3691390ac" />
      </td>
      <td>
       <img width="336" height="752" alt="ss3" src="https://github.com/user-attachments/assets/1bdc64f3-d9c4-4a50-bf1a-67852e77eba3" />
      </td>
    </tr>
  </table>
</div>

## 🛠 Tech Stack
- **Jetpack Compose** – 100% declarative UI with Material 3 and Dynamic Color support.
- **Navigation Compose** – Type-safe routing using Kotlin Serialization.
- **Dagger-Hilt** – Dependency Injection with modularized providers.
- **Room** – Local persistence for **Offline-First** capability and Single Source of Truth (SSoT).
- **Retrofit + OkHttp** – Network layer with custom Call Adapters for automatic `Resource<T>` wrapping.
- **Google Maps Compose** – Interactive map with custom JSON styles and geospatial polygon overlays.
- **Coroutines & Flow** – Reactive data streams with an **MVVM/MVI hybrid approach** (StateFlow + Channels).
- **Clean Architecture** – Strictly decoupled layers (Data, Domain, Presentation) with single-responsibility UseCases.
- **Offline Resilience** – Resilient recovery with intelligent data pre-fetching and localized error handling.
- **Multi-Language Support** – Fully localized in English and Turkish using a custom `UiText` wrapper.

## 📦 Project Architecture & Flow
The project follows **Clean Architecture** principles with a strict separation of concerns. Below is the high-level data flow and architectural diagram:

```mermaid
graph TD
    %% Layer Definitions
    subgraph UI_Layer [Presentation Layer - Compose UI]
        A[Splash Screen] --> B[Park Map Screen - Home]
        B --> C[Park Detail Screen]
    end

    subgraph State_Management [ViewModel & UI State]
        B_VM[ParkMapViewModel]
        C_VM[ParkDetailViewModel]
        B_State[ParkMapUiState]
        C_State[ParkDetailUiState]
        Events[UiEvent - Snackbar/Nav]
    end

    subgraph Domain_Layer [Domain Layer - Business Logic]
        UC1[FetchParksUseCase]
        UC2[FilterParksUseCase]
        UC3[FetchParkDetailUseCase]
        UC4[ObserveParkDetailUseCase]
        UC5[ToggleFavoriteUseCase]
    end

    subgraph Data_Layer [Data Layer - Repository & Sources]
        Repo[ParkRepositoryImpl]
        API[ParkApiService - Retrofit]
        DB[ParkDao - Room]
    end

    %% Flow Connections
    A -- triggers --> UC1
    UC1 -- fetch & cache --> Repo
    Repo -- calls --> API
    API -- returns JSON --> Repo
    Repo -- saves to --> DB

    B_VM -- observes --> UC4
    B_VM -- filters via --> UC2
    UC4 -- stream --> DB

    C_VM -- actions --> UC5
    C_VM -- refresh detail --> UC3
    UC3 -- network sync --> Repo

    %% Data Flow Directions
    Repo -.-> B_State
    B_VM -.-> B_State
    B_State -.-> B
    C_VM -.-> C_State
    C_State -.-> C
    B_VM -- events --> Events
    Events -.-> B
```

### Folder Structure
```
com.brkckr.parkv2/
├── data/
│   ├── local/          # Room DB, DAO, Entities (Offline Storage)
│   ├── remote/         # Retrofit API, Response models, Call Adapters
│   ├── mapper/         # Data ↔ Domain Layer conversion
│   └── repository/     # Repository Implementations (SSoT Logic)
├── domain/
│   ├── model/          # Clean Data Classes (Park, ParkDetail)
│   ├── repository/     # Domain interfaces
│   └── usecase/        # Single-responsibility Business Logic
├── presentation/
│   ├── splash/         # Onboarding & Initial Sync
│   ├── park_map/       # Map UI, Horizontal Pager, Search
│   ├── park_detail/    # Park Details & Polygon display
│   ├── navigation/     # Type-safe Route definitions
│   └── common/         # UiEvent, UiText, Shared UI Logic
├── di/                 # Decoupled Hilt Modules
└── ui/
    ├── theme/          # Material 3 Theme & Dynamic Colors
    └── components/     # Global Reusable UI Components (Chips, Loaders)
```

## ⚙️ Setup
1. Add your **Google Maps API Key** to `local.defaults.properties` in the root directory.
2. Open the project in **Android Studio** and click **Run**## ⚙️ Installation & Setup




