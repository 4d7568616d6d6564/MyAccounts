package com.mkdevelopment.myaccounts.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category_table ORDER BY id ASC")
    fun getAllDataByIdASC(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM category_table ORDER BY id DESC")
    fun getAllDataByIdDESC(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM category_table ORDER BY title ASC")
    fun getAllDataByTextASC(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM category_table ORDER BY title DESC")
    fun getAllDataByTextDESC(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM category_table WHERE id = :id")
    suspend fun getCategoryById(id: Int): CategoryEntity

    @Query("SELECT EXISTS(SELECT 1 FROM category_table WHERE title = :title LIMIT 1)")
    fun checkCategoryTitle(title: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(categoryEntity: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDataLastID(categoryEntity: CategoryEntity): Long

    @Update
    suspend fun updateData(categoryEntity: CategoryEntity)

    @Delete
    suspend fun deleteData(categoryEntity: CategoryEntity)

    @Query("DELETE FROM category_table")
    suspend fun deleteAllData()
}