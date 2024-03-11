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
import kotlinx.coroutines.withContext

class AccountDataViewModel(application: Application) : AndroidViewModel(application) {
    private val accountDao = AccountsDatabase.getDatabase(application).accountDao()

    suspend fun getDataByID(accountId: Int): List<AccountEntity> {
        return withContext(Dispatchers.IO) {
            accountDao.getDataById(accountId)
        }
    }


    fun getAllDataByIdASC(selectedCategoryId: Int): LiveData<List<AccountEntity>> {
        return accountDao.getAllDataByIdASC(selectedCategoryId)
    }

    fun getAllDataByIdDESC(selectedCategoryId: Int): LiveData<List<AccountEntity>> {
        return accountDao.getAllDataByIdDESC(selectedCategoryId)
    }

    fun getAllDataByTitleASC(selectedCategoryId: Int): LiveData<List<AccountEntity>> {
        return accountDao.getAllDataByTitleASC(selectedCategoryId)
    }

    fun getAllDataByTitleDESC(selectedCategoryId: Int): LiveData<List<AccountEntity>> {
        return accountDao.getAllDataByTitleDESC(selectedCategoryId)
    }

    fun getTotalAccountCount(): LiveData<Int> {
        return accountDao.getTotalCount()
    }

    fun updateAccountsCategory(oldCategoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val accounts = accountDao.getAccountsByCategoryId(oldCategoryId)
            for (account in accounts) {
                account.categoryId = 0
                accountDao.updateData(account)
            }
        }
    }


    fun insertData(accountEntity: AccountEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            accountDao.insertData(accountEntity)
        }
    }

    fun updateData(accountEntity: AccountEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            accountDao.updateData(accountEntity)
        }
    }

    fun deleteData(accountEntity: AccountEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            accountDao.deleteData(accountEntity)
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            accountDao.deleteAllData()
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<AccountEntity>> {
        return accountDao.searchDatabase(searchQuery)
    }
}