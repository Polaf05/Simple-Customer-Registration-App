package com.exam.simplecustomerregistrationapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.exam.simplecustomerregistrationapp.data.database.entity.Customer
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customer: Customer)

    @Query("SELECT * FROM customers ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Customer>>

    @Delete
    suspend fun delete(customer: Customer)

    @Query(
        """
        UPDATE customers
        SET 
            firstName = :firstName,
            lastName = :lastName,
            mobileNumber = :mobileNumber,
            address = :address,
            photoUri = :photoUri
        WHERE id = :id
    """
    )
    suspend fun update(
        id: Long,
        firstName: String,
        lastName: String,
        mobileNumber: String,
        address: String,
        photoUri: String?
    )

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Int): Customer?
}