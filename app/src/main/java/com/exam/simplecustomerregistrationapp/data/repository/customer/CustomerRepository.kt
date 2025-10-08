package com.exam.simplecustomerregistrationapp.data.repository.customer

import com.exam.simplecustomerregistrationapp.data.database.entity.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getCustomers(): Flow<List<Customer>>
    suspend fun insertCustomer(customer: Customer)
    suspend fun deleteCustomer(customer: Customer)
    suspend fun updateCustomer(customerId: Long, customer: Customer)
    suspend fun getCustomerById(id: Int): Customer?
}