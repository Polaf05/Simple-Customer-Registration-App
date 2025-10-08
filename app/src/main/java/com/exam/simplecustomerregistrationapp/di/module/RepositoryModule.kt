package com.exam.simplecustomerregistrationapp.di.module

import com.exam.simplecustomerregistrationapp.data.repository.customer.CustomerRepository
import com.exam.simplecustomerregistrationapp.data.repository.customer.CustomerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCustomerRepository(
        impl: CustomerRepositoryImpl
    ): CustomerRepository
}