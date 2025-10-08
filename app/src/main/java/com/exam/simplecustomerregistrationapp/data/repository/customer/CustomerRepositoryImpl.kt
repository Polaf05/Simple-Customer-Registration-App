package com.exam.simplecustomerregistrationapp.data.repository.customer

import com.exam.simplecustomerregistrationapp.data.database.entity.Customer
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val localDataSource: CustomerLocalDataSource
) : CustomerRepository {
    override fun getCustomers() = localDataSource.getCustomers()
    override suspend fun insertCustomer(customer: Customer) = localDataSource.insertCustomer(customer)
    override suspend fun deleteCustomer(customer: Customer) = localDataSource.deleteCustomer(customer)
    override suspend fun updateCustomer(customerId:Long,customer: Customer) = localDataSource.updateCustomer(customerId ,customer)
    override suspend fun getCustomerById(id: Int): Customer? = localDataSource.getCustomerById(id)

}