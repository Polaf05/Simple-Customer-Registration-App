package com.exam.simplecustomerregistrationapp.ui.model

sealed class Screen(val route: String) {
    object CustomerList : Screen("customer_list")
    object CustomerRegistration : Screen("customer_registration")
}
