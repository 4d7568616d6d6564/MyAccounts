package com.mkdevelopment.myaccounts.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AccountDao {
    @Query("SELECT * FROM account_table WHERE (:categoryId != 0 AND categoryId LIKE :categoryId) OR (:categoryId = 0) ORDER BY id ASC")
    fun getAllDataByIdASC(categoryId:Int): LiveData<List<AccountEntity>>

    @Query("SELECT * FROM account_table WHERE (:categoryId != 0 AND categoryId LIKE :categoryId) OR (:categoryId = 0) ORDER BY id DESC")
    fun getAllDataByIdDESC(categoryId:Int): LiveData<List<AccountEntity>>

    @Query("SELECT * FROM account_table WHERE (:categoryId != 0 AND categoryId LIKE :categoryId) OR (:categoryId = 0) ORDER BY title ASC")
    fun getAllDataByTitleASC(categoryId:Int): LiveData<List<AccountEntity>>

    @Query("SELECT * FROM account_table WHERE (:categoryId != 0 AND categoryId LIKE :categoryId) OR (:categoryId = 0) ORDER BY title DESC")
    fun getAllDataByTitleDESC(categoryId:Int): LiveData<List<AccountEntity>>

    @Query("SELECT * FROM account_table ORDER BY addedTime ASC")
    fun getAllDataByAddedTimeASC(): LiveData<List<AccountEntity>>

    @Query("SELECT * FROM account_table ORDER BY addedTime DESC")
    fun getAllDataByAddedTimeDESC(): LiveData<List<AccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(accountEntity: AccountEntity)

    @Update
    suspend fun updateData(accountEntity: AccountEntity)

    @Delete
    suspend fun deleteData(accountEntity: AccountEntity)

    @Query("SELECT COUNT(*) FROM account_table")
    fun getTotalCount(): LiveData<Int>

    @Query("DELETE FROM account_table")
    suspend fun deleteAllData()

    @Query("SELECT * FROM account_table WHERE title LIKE '%' || :searchQuery || '%' OR username LIKE '%' || :searchQuery || '%' OR name LIKE '%' || :searchQuery || '%' OR password LIKE '%' || :searchQuery || '%' OR surname LIKE '%' || :searchQuery || '%' OR email LIKE '%' || :searchQuery || '%' OR phone LIKE '%' || :searchQuery || '%' OR other LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun searchDatabase(searchQuery: String): LiveData<List<AccountEntity>>


    @Query("SELECT * FROM account_table WHERE categoryId LIKE :searchID ORDER BY id DESC")
    fun searchDatabaseByID(searchID: Int): LiveData<List<AccountEntity>>
}