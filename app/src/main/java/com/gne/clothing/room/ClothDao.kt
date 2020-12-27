package com.gne.clothing.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gne.clothing.vo.Cloth

@Dao
interface ClothDao {
    @Query("Select * from cloth")
    fun getAllCloths(): LiveData<List<Cloth>>

    @Query("Select * from cloth where _type=:clothType")
    fun getAllByClothType(clothType: String): LiveData<List<Cloth>>

    @Query("Select * from cloth where _imgPath=:path")
    fun getClothByPath(path:String): LiveData<Cloth>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloth(cloth:Cloth):Long

    @Query("UPDATE cloth SET _isFavourite = :isFavourite WHERE _imgPath IN (:paths)")
    suspend fun updateFavourite(paths:List<String>, isFavourite:Boolean)
}