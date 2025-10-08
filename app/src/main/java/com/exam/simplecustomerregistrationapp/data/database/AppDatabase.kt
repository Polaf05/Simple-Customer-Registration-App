package com.exam.simplecustomerregistrationapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.exam.simplecustomerregistrationapp.data.database.dao.CustomerDao
import com.exam.simplecustomerregistrationapp.data.database.entity.Customer

@Database(entities = [Customer::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao

    companion object {
        const val DB_NAME = "customer_db"
    }
}