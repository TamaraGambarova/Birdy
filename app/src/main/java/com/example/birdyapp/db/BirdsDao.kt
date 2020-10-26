package com.example.birdyapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BirdsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rates: List<OfflineBirdsModel>)

    @Query("select * from offline_birds")
    fun getBalances() : LiveData<List<OfflineBirdsModel>>
}