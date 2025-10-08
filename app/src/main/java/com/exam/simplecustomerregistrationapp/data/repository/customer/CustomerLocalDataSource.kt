package com.exam.simplecustomerregistrationapp.data.repository.customer

import com.exam.simplecustomerregistrationapp.data.database.dao.CustomerDao
import com.exam.simplecustomerregistrationapp.data.database.entity.Customer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CustomerLocalDataSource @Inject constructor(
    private val customerDao: CustomerDao
) {
    fun getCustomers(): Flow<List<Customer>> = customerDao.getAll()

    suspend fun insertCustomer(customer: Customer) = customerDao.insert(customer)

    suspend fun deleteCustomer(customer: Customer) = customerDao.delete(customer)

    suspend fun updateCustomer(customerId: Long, customer: Customer) {
        customerDao.update(
            id = customerId,
            firstName = customer.firstName,
            lastName = customer.lastName,
            mobileNumber = customer.mobileNumber,
            address = customer.address,
            photoUri = customer.photoUri
        )
    }

    suspend fun getCustomerById(id: Int): Customer? = customerDao.getCustomerById(id)
}