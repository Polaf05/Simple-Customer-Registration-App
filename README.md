# 🧭 Simple Customer Registration App

A modern Android app built with **Jetpack Compose**, **Hilt**, and **Room** to register, edit, and manage customer data — designed for performance, clarity, and scalability.

---

## ✨ Features

✅ Reactive **Flow-based architecture**
✅ Add, edit, and delete customer records
✅ Image selection with **persistent URI handling**
✅ Field validation with **PH-style mobile auto-formatting**
✅ Dialog confirmations for delete
✅ **Keyboard auto-hide** on outside tap
✅ **Empty & loading states** with Material 3 UI
✅ Built using **MVVM + Repository** pattern

---

## ⚙️ Tech Stack

| Layer          | Technology                    | Purpose                      |
| -------------- | ----------------------------- | ---------------------------- |
| **UI**         | Jetpack Compose (Material 3)  | Declarative UI framework     |
| **DI**         | Hilt (Dagger)                 | Dependency injection         |
| **Data**       | Room Database                 | Local data persistence       |
| **Async**      | Kotlin Coroutines + StateFlow | Reactive UI state management |
| **Images**     | Coil                          | Lightweight image loading    |
| **Navigation** | Jetpack Navigation Compose    | Route-safe navigation system |

---

## 🧱 Architecture Overview

```
UI (Composable Screens)
│
│  observes
▼
ViewModel (CustomerViewModel)
│
│  calls
▼
Repository (CustomerRepository)
│
│  accesses
▼
Data Layer (Room DAO)
```

All UI states are reactive — once the repository updates, the UI refreshes automatically.

---

## 🎨 UI Overview

### 📋 Customer List Screen

* Displays all customers in a lazy list.
* Swipe to reveal delete button.
* Tap item to **edit**.
* Shows **empty state** when no customers exist.

### 📝 Registration Screen

* Add or edit customer details.
* Image picker (Android 12–13 compatible).
* Form validation with error feedback.
* Auto-hide keyboard and smooth UX.

---

## 🧠 State Management

### `CustomerUiState`

Represents screen-level state:

```kotlin
sealed class CustomerUiState {
    object Loading : CustomerUiState()
    data class Success(val customers: List<Customer>) : CustomerUiState()
    data class Error(val message: String) : CustomerUiState()
}
```

### `CustomerFormState`

Encapsulates input validation:

```kotlin
data class CustomerFormState(
    val firstName: String = "",
    val lastName: String = "",
    val mobileNumber: String = "",
    val address: String = "",
    val photoUri: String? = null,
    val isValid: Boolean = false,
    val errorMessage: String? = null
)
```
---

##🧪 Unit Testing

**Basic unit tests** ensure your data flow and logic behave correctly before UI integration.
Below are simplified examples for testing both the **Repository** and **ViewModel** layers.

✅ Test highlights:

Uses MockK for mocking DAO and repository.
Uses runTest for coroutine scope safety.
Ensures repository calls are triggered correctly.
Validates ViewModel state and logic without touching UI.

---

## 🚀 Quick Start

1. Clone the repository:

   ```bash
   git clone https://github.com/<your-username>/SimpleCustomerRegistrationApp.git
   cd SimpleCustomerRegistrationApp
   ```
2. Open in **Android Studio (Giraffe or later)**
3. Run the app on an emulator or device running **Android 8.0+**

---

**Crafted with ❤️ and Kotlin by Kevin Paular**
