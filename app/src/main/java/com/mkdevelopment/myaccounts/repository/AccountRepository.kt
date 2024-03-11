package com.mkdevelopment.myaccounts.repository

import androidx.lifecycle.LiveData
import com.mkdevelopment.myaccounts.database.AccountDao
import com.mkdevelopment.myaccounts.database.AccountEntity

class AccountRepository(private val accountDao: AccountDao) {
    suspend fun getDataByID(accountId: Int): List<AccountEntity> {
        return accountDao.getDataById(accountId)
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

    suspend fun getAccountsByCategoryId(categoryId: Int): List<AccountEntity> {
        return accountDao.getAccountsByCategoryId(categoryId)
    }

    suspend fun insertData(accountEntity: AccountEntity) {
        accountDao.insertData(accountEntity)
    }

    suspend fun updateData(accountEntity: AccountEntity) {
        accountDao.updateData(accountEntity)
    }

    suspend fun deleteData(accountEntity: AccountEntity) {
        accountDao.deleteData(accountEntity)
    }

    suspend fun deleteAllData() {
        accountDao.deleteAllData()
    }

    fun searchDatabase(searchQuery: String): LiveData<List<AccountEntity>> {
        return accountDao.searchDatabase(searchQuery)
    }
}