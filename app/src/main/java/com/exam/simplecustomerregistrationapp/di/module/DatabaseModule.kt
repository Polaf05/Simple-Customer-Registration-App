package com.exam.simplecustomerregistrationapp.di.module

import android.content.Context
import androidx.room.Room
import com.exam.simplecustomerregistrationapp.data.database.AppDatabase
import com.exam.simplecustomerregistrationapp.data.database.dao.CustomerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DB_NAME)
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    fun provideCustomerDao(db: AppDatabase): CustomerDao = db.customerDao()
}