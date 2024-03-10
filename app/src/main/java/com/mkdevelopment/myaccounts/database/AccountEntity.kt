package com.mkdevelopment.myaccounts.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_table")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryId: Int = -1,
    val title: String = "",
    val name: String = "",
    val surname: String = "",
    val gender: String = "",
    val birthday: String = "",
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val phone: String = "",
    val recoveryEmail: String = "",
    val recoveryPhone: String = "",
    val securityQuestion: String = "",
    val securityQuestionAnswer: String = "",
    val address: String = "",
    val other: String = "",
    val addedTime: String = "",
    val updatedTime: String = "",
    val iconPosition: Int = -1,

    )