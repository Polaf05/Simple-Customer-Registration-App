package com.exam.simplecustomerregistrationapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val firstName: String,
    val lastName: String,
    val mobileNumber: String,
    val address: String,
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)