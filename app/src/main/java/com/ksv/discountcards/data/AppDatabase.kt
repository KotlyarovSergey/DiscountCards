package com.ksv.discountcards.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ksv.discountcards.entity.Card

@Database(entities = [Card::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getCardsDao(): CardsDao
}