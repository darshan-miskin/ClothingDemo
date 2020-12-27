package com.gne.clothing.vo

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gne.clothing.adapters.ClothingPagerAdapter
import java.io.Serializable

@Entity
data class Cloth(@ColumnInfo(name = "_id") var id:Long,
                 @ColumnInfo(name = "_type") var type:String,
                 @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "_imgPath") var imgPath:String,
                 @ColumnInfo(name = "_isFavourite") @Bindable var isFavourite:Boolean=false) : Serializable, BaseObservable() {

//    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")
//    var id:Long?=null
}