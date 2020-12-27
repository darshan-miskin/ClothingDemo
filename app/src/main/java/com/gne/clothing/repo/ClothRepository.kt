package com.gne.clothing.repo

import android.util.Log
import androidx.lifecycle.LiveData
import com.gne.clothing.adapters.ClothingPagerAdapter
import com.gne.clothing.room.ClothDao
import com.gne.clothing.vo.Cloth

class ClothRepository(private val clothDao: ClothDao) {
    val allClothes: LiveData<List<Cloth>> = clothDao.getAllCloths()
    val allShirts: LiveData<List<Cloth>> = clothDao.getAllByClothType(ClothingPagerAdapter.ClothType.Shirts.toString())
    val allPants: LiveData<List<Cloth>> = clothDao.getAllByClothType(ClothingPagerAdapter.ClothType.Pants.toString())

    suspend fun insert(cloth: Cloth){
        val inserted=clothDao.insertCloth(cloth)
    }

    suspend fun updateFavourites(paths:List<String>, isFavourite:Boolean){
        clothDao.updateFavourite(paths,isFavourite)
    }

}