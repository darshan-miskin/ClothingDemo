package com.gne.clothing.screens

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gne.clothing.R
import com.gne.clothing.databinding.FragmentImageBinding
import com.gne.clothing.vo.Cloth
import com.squareup.picasso.Picasso

private const val ARG_PARAM1 = "param1"

class ImageFragment : Fragment() {
    private lateinit var cloth: Cloth
    private lateinit var binding:FragmentImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cloth = it.get(ARG_PARAM1) as Cloth
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_image, container, false)

        Picasso.get().load(cloth.imgPath).fit().into(binding.imageView)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(cloth: Cloth) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, cloth)
                }
            }
    }
}