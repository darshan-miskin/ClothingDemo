package com.gne.clothing.screens

import android.R.attr.end
import android.R.attr.start
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.gne.clothing.repo.ClothRepository
import com.gne.clothing.room.ClothDatabase
import com.gne.clothing.vo.Cloth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class ClothesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ClothRepository
    val allShirtsList: LiveData<List<Cloth>>
    val allPantsList: LiveData<List<Cloth>>

    private val favShirtSet=HashSet<Int>()
    private val favPantSet=HashSet<Int>()

    private val _randomPair=MutableLiveData<Pair<Int, Int>>()
    val randomPair:LiveData<Pair<Int, Int>>
        get() = _randomPair

    private val _isPantFavourite=MutableLiveData<Boolean>()
    private val isPantFavourite:LiveData<Boolean>
        get() = _isPantFavourite
    private val _isShirtFavourite=MutableLiveData<Boolean>()
    private val isShirtFavourite:LiveData<Boolean>
        get() = _isShirtFavourite

    private val _isPantsAvailable=MutableLiveData<Boolean>()
    private val _isShirtsAvailable=MutableLiveData<Boolean>()
    private val isPantsAvailable:LiveData<Boolean>
        get() = _isPantsAvailable
    private val isShirtsAvailable:LiveData<Boolean>
        get() = _isShirtsAvailable

    init {
        val dao=ClothDatabase.getDatabase(application, viewModelScope).ClothDao()
        repository= ClothRepository(dao)
        allShirtsList=repository.allShirts
        allPantsList=repository.allPants
    }

    fun insert(cloth: Cloth)=viewModelScope.launch(Dispatchers.IO){
        repository.insert(cloth)
    }

    fun setFavourite(shirtPos: Int, pantPos: Int):Boolean{
        val shirt = allShirtsList.value!!.get(shirtPos)
        val pant = allPantsList.value!!.get(pantPos)
        var finalIsFav=false
        viewModelScope.launch(Dispatchers.IO) {
            val ids = ArrayList<String>()
            if(shirt.isFavourite && pant.isFavourite){
                finalIsFav=false
                ids.add(pant.imgPath)
                ids.add(shirt.imgPath)
            }
            else if(!shirt.isFavourite && !pant.isFavourite){
                finalIsFav=true
                ids.add(pant.imgPath)
                ids.add(shirt.imgPath)
            }
            else if(!shirt.isFavourite && pant.isFavourite){
                finalIsFav=true
                ids.add(shirt.imgPath)
            }
            else if(!pant.isFavourite && shirt.isFavourite){
                finalIsFav=true
                ids.add(pant.imgPath)
            }
            repository.updateFavourites(ids, finalIsFav)
        }
        _isShirtFavourite.value=finalIsFav
        _isPantFavourite.value=finalIsFav
        return finalIsFav
    }

    fun isShowFavFab(): LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()

        mediatorLiveData.addSource(isPantsAvailable) {
            mediatorLiveData.value = it && isShirtsAvailable.value == true
        }

        mediatorLiveData.addSource(isShirtsAvailable) {
            mediatorLiveData.value = it && isPantsAvailable.value == true
        }

        return mediatorLiveData
    }

    fun isFavouriteCombo(): LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()

        mediatorLiveData.addSource(isPantFavourite) {
            mediatorLiveData.value = it && isShirtFavourite.value == true
        }

        mediatorLiveData.addSource(isShirtFavourite) {
            mediatorLiveData.value = it && isPantFavourite.value == true
        }

        return mediatorLiveData
    }

    fun isPantFavourite(position: Int){
        _isPantFavourite.value=allPantsList.value?.get(position)?.isFavourite
    }

    fun isShirtFavourite(position: Int){
        _isShirtFavourite.value=allShirtsList.value?.get(position)?.isFavourite
    }

    fun setIsPantsAvailable(isAvailable: Boolean){
        _isPantsAvailable.value=isAvailable
    }

    fun setIsShirtsAvailable(isAvailable: Boolean){
        _isShirtsAvailable.value=isAvailable
    }

    fun updateFavouriteShirts(){
        for(i in allShirtsList.value!!.indices){
            val shirt= allShirtsList.value!!.get(i)
            if(shirt.isFavourite){
                favShirtSet.add(i)
            }
        }
    }

    fun updateFavouritePants(){
        for(i in allPantsList.value!!.indices){
            val shirt= allPantsList.value!!.get(i)
            if(shirt.isFavourite){
                favPantSet.add(i)
            }
        }
    }

    fun randomize(){
        val rand = Random()
        val rangeShirt:Int = allShirtsList.value?.size!!-1
        val rangePant:Int = allPantsList.value?.size!!-1

        var randomShirt:Int = rand.nextInt(rangeShirt) + 1
        var randomPant:Int = rand.nextInt(rangePant) + 1

        while (favShirtSet.contains(randomShirt) && favPantSet.contains(randomPant) &&
            (randomPair.value?.first!=randomShirt || randomPair.value?.second!=randomPant)) {
            randomShirt = rand.nextInt(rangeShirt) + 1
            randomPant = rand.nextInt(rangePant) + 1
        }
        _randomPair.value=Pair(randomShirt, randomPant)
    }
}