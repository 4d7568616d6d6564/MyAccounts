package com.mkdevelopment.myaccounts.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mkdevelopment.myaccounts.database.CategoryDatabase
import com.mkdevelopment.myaccounts.database.CategoryEntity
import com.mkdevelopment.myaccounts.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryDataViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryDao = CategoryDatabase.getDatabase(application).categoryDao()
    private val repository: CategoryRepository = CategoryRepository(categoryDao)
    val getAllDataByIdASC: LiveData<List<CategoryEntity>> = categoryDao.getAllDataByIdASC()

    fun checkCategoryTitle(title: String): Boolean {
        return repository.checkCategoryTitle(title)
    }

    suspend fun insertDataLastID(categoryEntity: CategoryEntity): Long {
        return withContext(Dispatchers.IO) {
            repository.insertDataLastID(categoryEntity)
        }
    }


    fun deleteData(categoryEntity: CategoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteData(categoryEntity)
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllData()
        }
    }

}