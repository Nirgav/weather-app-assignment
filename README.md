# WeatherApp

A cross-platform weather application built with Kotlin Multiplatform Mobile (KMM) that provides current weather information and forecasts.

## Features

- **Multiplatform Support**: Runs on both Android and iOS
- **Current Weather**: View current weather conditions
- **Weather Forecast**: Get detailed weather forecasts
- **Location-based**: Automatically detects your location or search by city
- **Caching**: Offline support with local data caching

## Tech Stack

- **Kotlin Multiplatform Mobile (KMM)** for shared business logic
- **Ktor** for network requests
- **SQLDelight** for local caching
- **Koin** for dependency injection
- **Coroutines** for asynchronous operations
- **Kotlinx.serialization** for JSON parsing

## Project Structure

- `shared/`: Contains shared Kotlin code
  - `commonMain/`: Platform-agnostic code
  - `androidMain/`: Android-specific code
  - `iosMain/`: iOS-specific code
- `androidApp/`: Android application module
- `iosApp/`: iOS application module

## Getting Started

1. Clone the repository
2. Open the project in Android Studio or Xcode
3. Sync Gradle files
4. Build and run the app on your preferred platform

## Requirements

- Android Studio (for Android development)
- Xcode (for iOS development)
- Kotlin 1.9.0 or higher
- JDK 11 or higher

## License

This project is licensed under the MIT License - see the LICENSE file for details.
