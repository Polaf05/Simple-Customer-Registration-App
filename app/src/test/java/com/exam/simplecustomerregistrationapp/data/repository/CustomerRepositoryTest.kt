package com.exam.simplecustomerregistrationapp.data.repository

import com.assesment.gweather.rule.MainDispatcherRule
import com.exam.simplecustomerregistrationapp.data.database.entity.Customer
import com.exam.simplecustomerregistrationapp.data.repository.customer.CustomerLocalDataSource
import com.exam.simplecustomerregistrationapp.data.repository.customer.CustomerRepositoryImpl
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CustomerRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: CustomerRepositoryImpl
    private val localDataSource: CustomerLocalDataSource = mockk(relaxed = true)

    private val fakeCustomer = Customer(
        id = 1,
        firstName = "John",
        lastName = "Doe",
        mobileNumber = "09123456789",
        address = "123 Main St",
        photoUri = "path/to/photo"
    )

    @Before
    fun setup() {
        repository = CustomerRepositoryImpl(localDataSource)
    }

    @Test
    fun `insertCustomer delegates to localDataSource`() = runTest {
        coEvery { localDataSource.insertCustomer(any()) } just Runs

        repository.insertCustomer(fakeCustomer)

        coVerify(exactly = 1) { localDataSource.insertCustomer(fakeCustomer) }
    }

    @Test
    fun `getCustomers returns flow of customers from localDataSource`() = runTest {
        val fakeList = listOf(fakeCustomer)
        every { localDataSource.getCustomers() } returns flowOf(fakeList)

        val result = repository.getCustomers().first()

        assertEquals(1, result.size)
        assertEquals("John", result.first().firstName)
        coVerify { localDataSource.getCustomers() }
    }

    @Test
    fun `deleteCustomer delegates to localDataSource`() = runTest {
        coEvery { localDataSource.deleteCustomer(any()) } just Runs

        repository.deleteCustomer(fakeCustomer)

        coVerify { localDataSource.deleteCustomer(fakeCustomer) }
    }
}