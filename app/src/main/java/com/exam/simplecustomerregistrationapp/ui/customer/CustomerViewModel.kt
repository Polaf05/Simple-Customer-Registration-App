package com.exam.simplecustomerregistrationapp.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exam.simplecustomerregistrationapp.data.database.entity.Customer
import com.exam.simplecustomerregistrationapp.data.repository.customer.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {

    val uiState: StateFlow<CustomerUiState> = repository.getCustomers()
        .map<List<Customer>, CustomerUiState> { customers ->
            CustomerUiState.Success(customers)
        }
        .catch { emit(CustomerUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CustomerUiState.Loading
        )

    private val _formState = MutableStateFlow(CustomerFormState())
    val formState: StateFlow<CustomerFormState> = _formState.asStateFlow()

    fun onFormChange(
        firstName: String = _formState.value.firstName,
        lastName: String = _formState.value.lastName,
        mobileNumber: String = _formState.value.mobileNumber,
        address: String = _formState.value.address,
        photoUri: String? = _formState.value.photoUri
    ) {
        _formState.value = CustomerFormState(
            firstName = firstName,
            lastName = lastName,
            mobileNumber = mobileNumber,
            address = address,
            photoUri = photoUri
        ).validate()
    }

    suspend fun loadCustomer(customerId: String) {
        try {
            val customer = repository.getCustomerById(customerId.toInt())
            _formState.value = CustomerFormState(
                firstName = customer?.firstName ?: "",
                lastName = customer?.lastName ?: "",
                mobileNumber = customer?.mobileNumber ?: "",
                address = customer?.address ?: "",
                photoUri = customer?.photoUri,
                isValid = true
            )
        } catch (e: Exception) {
            _formState.value = CustomerFormState(
                errorMessage = e.message ?: "Failed to load customer."
            )
        }
    }

    fun saveCustomer(customerId: String? = null) {
        val form = _formState.value.validate()
        if (!form.isValid) {
            _formState.value = form
            return
        }

        viewModelScope.launch {
            try {
                val customer = Customer(
                    firstName = form.firstName,
                    lastName = form.lastName,
                    mobileNumber = form.mobileNumber,
                    address = form.address,
                    photoUri = form.photoUri
                )

                if (customerId.isNullOrEmpty()) {
                    repository.insertCustomer(customer)
                } else {
                    repository.updateCustomer(customerId.toLong(), customer)
                }

                _formState.value = CustomerFormState()
            } catch (e: Exception) {
                _formState.value = form.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }
}