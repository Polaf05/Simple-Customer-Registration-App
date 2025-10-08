package com.exam.simplecustomerregistrationapp.ui.customer

data class CustomerFormState(
    val firstName: String = "",
    val lastName: String = "",
    val mobileNumber: String = "",
    val address: String = "",
    val photoUri: String? = null,
    val isValid: Boolean = false,
    val errorMessage: String? = null
) {
    fun validate(): CustomerFormState {
        if (firstName.isBlank() || lastName.isBlank() || mobileNumber.isBlank() || address.isBlank()) {
            return copy(isValid = false, errorMessage = "All fields are required.")
        }

        val cleaned = mobileNumber.replace(" ", "").replace("-", "")
        val formattedNumber = when {
            cleaned.startsWith("+63") -> cleaned
            cleaned.startsWith("0") -> cleaned.replaceFirst("0", "+63")
            else -> "+63$cleaned"
        }

        val isValidNumber = Regex("^\\+639\\d{9}$").matches(formattedNumber)

        return if (!isValidNumber) {
            copy(isValid = false, errorMessage = "Invalid PH mobile number.")
        } else {
            copy(isValid = true, errorMessage = null, mobileNumber = formattedNumber)
        }
    }
}