package com.exam.simplecustomerregistrationapp.ui.customer

import app.cash.turbine.test
import com.assesment.gweather.rule.MainDispatcherRule
import com.exam.simplecustomerregistrationapp.data.database.entity.Customer
import com.exam.simplecustomerregistrationapp.data.repository.customer.CustomerRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CustomerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CustomerViewModel
    private val repository: CustomerRepository = mockk()

    private val fakeCustomers = listOf(
        Customer(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            mobileNumber = "09123456789",
            address = "Manila",
            photoUri = null
        )
    )

    @Before
    fun setup() {
        coEvery { repository.getCustomers() } returns flowOf(fakeCustomers)
        viewModel = CustomerViewModel(repository)
    }

    @Test
    fun `init loads customers successfully`() = runTest {
        viewModel.uiState.test {
            val first = awaitItem()
            val result = if (first is CustomerUiState.Loading) awaitItem() else first

            assertTrue(result is CustomerUiState.Success)
            val success = result as CustomerUiState.Success
            assertEquals(1, success.customers.size)
            assertEquals("John", success.customers[0].firstName)
        }
    }

    @Test
    fun `form validation fails when required fields are empty`() = runTest {
        viewModel.onFormChange(
            firstName = "", lastName = "", mobileNumber = "", address = ""
        )
        val form = viewModel.formState.value
        assertFalse(form.isValid)
        assertEquals("All fields are required.", form.errorMessage)
    }

    @Test
    fun `form validation fails when mobile number is invalid`() = runTest {
        viewModel.onFormChange(
            firstName = "John", lastName = "Doe", mobileNumber = "09123", address = "Manila"
        )
        val form = viewModel.formState.value
        assertFalse(form.isValid)
        assertEquals("Invalid PH mobile number.", form.errorMessage)
    }

    @Test
    fun `saveCustomer inserts valid customer`() = runTest {
        coEvery { repository.insertCustomer(any()) } just Runs

        viewModel.onFormChange(
            firstName = "Jane", lastName = "Doe", mobileNumber = "09123456789", address = "Cebu"
        )
        viewModel.saveCustomer()

        coVerify { repository.insertCustomer(match { it.firstName == "Jane" }) }
        assertEquals(CustomerFormState(), viewModel.formState.value)
    }

    @Test
    fun `saveCustomer handles repository exception gracefully`() = runTest {
        coEvery { repository.insertCustomer(any()) } throws RuntimeException("Database error")

        viewModel.onFormChange(
            firstName = "Jane", lastName = "Doe", mobileNumber = "09123456789", address = "Cebu"
        )
        viewModel.saveCustomer()

        val form = viewModel.formState.value
        assertNotNull(form.errorMessage)
        assertEquals("Database error", form.errorMessage)
    }
}