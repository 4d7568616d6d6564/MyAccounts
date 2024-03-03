package com.mkdevelopment.myaccounts.repository

import androidx.lifecycle.LiveData
import com.mkdevelopment.myaccounts.database.CategoryDao
import com.mkdevelopment.myaccounts.database.CategoryEntity

class CategoryRepository(private val categoryDao: CategoryDao) {
    val getAllDataByIdASC: LiveData<List<CategoryEntity>> = categoryDao.getAllDataByIdASC()
    val getAllDataByIdDESC: LiveData<List<CategoryEntity>> = categoryDao.getAllDataByIdDESC()

    fun checkCategoryTitle(title: String): Boolean {
        return categoryDao.checkCategoryTitle(title)
    }

    suspend fun insertDataLastID(categoryEntity: CategoryEntity): Long {
        return categoryDao.insertDataLastID(categoryEntity)
    }

    suspend fun deleteData(categoryEntity: CategoryEntity) {
        categoryDao.deleteData(categoryEntity)
    }

    suspend fun deleteAllData() {
        categoryDao.deleteAllData()
    }
}