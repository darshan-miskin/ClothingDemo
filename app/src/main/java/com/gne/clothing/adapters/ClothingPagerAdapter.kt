package com.gne.clothing.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gne.clothing.screens.ImageFragment
import com.gne.clothing.vo.Cloth

class ClothingPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private var arrayList = emptyList<Cloth>()
    enum class ClothType{ Shirts, Pants }

    internal fun setClothes(clothes: List<Cloth>) {
        this.arrayList = clothes
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun createFragment(position: Int): Fragment {
        return ImageFragment.newInstance(arrayList.get(position))
    }

    override fun getItemId(position: Int): Long {
        return arrayList[position].id
    }

    override fun containsItem(itemId: Long): Boolean {
        for (cloth in arrayList){
            if(cloth.id.equals(itemId)){
                return true
            }
        }
        return false
    }
}