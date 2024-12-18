package com.ksv.discountcards.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
//    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "file_uri") val fileUri: String,
    val title: String
)
