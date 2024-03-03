package com.mkdevelopment.myaccounts.common

import com.mkdevelopment.myaccounts.database.CategoryEntity

interface ItemLongClickListener {
    fun onItemClick(categoryEntity: CategoryEntity)
}