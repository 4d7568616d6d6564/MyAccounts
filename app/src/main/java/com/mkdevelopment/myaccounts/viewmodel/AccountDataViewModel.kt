package com.mkdevelopment.myaccounts.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mkdevelopment.myaccounts.database.AccountEntity
import com.mkdevelopment.myaccounts.database.AccountsDatabase
import com.mkdevelopment.myaccounts.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountDataViewModel(application: Application) : AndroidViewModel(application) {
    private val accountDao = AccountsDatabase.getDatabase(application).accountDao()
    private val repository: AccountRepository = AccountRepository(accountDao)

    fun getAllDataByIdASC(selectedCategoryId: Int): LiveData<List<AccountEntity>> {
        return repository.getAllDataByIdASC(selectedCategoryId)
    }

    fun getAllDataByIdDESC(selectedCategoryId: Int): LiveData<List<AccountEntity>> {
        return repository.getAllDataByIdDESC(selectedCategoryId)
    }

    fun getAllDataByTitleASC(selectedCategoryId: Int): LiveData<List<AccountEntity>> {
        return repository.getAllDataByTitleASC(selectedCategoryId)
    }

    fun getAllDataByTitleDESC(selectedCategoryId: Int): LiveData<List<AccountEntity>> {
        return repository.getAllDataByTitleDESC(selectedCategoryId)
    }

    fun getTotalAccountCount(): LiveData<Int> {
        return repository.getTotalAccountCount()
    }

    fun insertData(accountEntity: AccountEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(accountEntity)
        }
    }

    fun deleteData(accountEntity: AccountEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteData(accountEntity)
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllData()
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<AccountEntity>> {
        return repository.searchDatabase(searchQuery)
    }
}