package com.exam.simplecustomerregistrationapp.ui.customer

import com.exam.simplecustomerregistrationapp.data.database.entity.Customer

sealed class CustomerUiState {
    object Loading : CustomerUiState()
    data class Success(val customers: List<Customer>) : CustomerUiState()
    data class Error(val message: String) : CustomerUiState()
}