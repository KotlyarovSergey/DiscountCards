package com.ksv.discountcards.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ksv.discountcards.entity.Card

@Dao
interface CardsDao {
    @Query("SELECT * FROM cards")
    suspend fun getAllCards(): List<Card>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardAtId(id: Long): Card

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inset(card: Card)

    @Update
    suspend fun update(card: Card)

    @Delete
    suspend fun delete(card: Card)
}