package com.gne.clothing.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.gne.clothing.R
import com.gne.clothing.adapters.ClothingPagerAdapter
import com.gne.clothing.anims.ZoomOutPageTransformer
import com.gne.clothing.databinding.ActivityMainBinding
import com.gne.clothing.vo.Cloth
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ClothesActivity : AppCompatActivity() {

    private val REQUEST_CODE_READ_INTERNAL=1111
    private val RESULT_LOAD_IMG=1523
    private val RESULT_CAMERA_IMG=1524

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: ClothesViewModel
    private lateinit var type:ClothingPagerAdapter.ClothType
    private lateinit var shirtPagerAdater:ClothingPagerAdapter
    private lateinit var pantPagerAdapter:ClothingPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel=ViewModelProvider(this).get(ClothesViewModel::class.java)

        shirtPagerAdater = ClothingPagerAdapter(this)
        pantPagerAdapter = ClothingPagerAdapter(this)

        binding.apply {
            pagerShirts.adapter=shirtPagerAdater
            pagerPants.adapter=pantPagerAdapter
            pagerShirts.setPageTransformer(ZoomOutPageTransformer())
            pagerPants.setPageTransformer(ZoomOutPageTransformer())
        }

        requestPermission()

        setObservers()

        setListeners()
    }

    private fun setObservers(){
        viewModel.isShowFavFab().observe(this, Observer {
            hideShowFab(it)
        })

        viewModel.allShirtsList.observe(this, Observer {
            val prevSize=shirtPagerAdater.itemCount
            if(prevSize<it.size) {
                viewModel.setIsShirtsAvailable(it.isNotEmpty())
                viewModel.updateFavouriteShirts()
                hideShowShirts(it.isEmpty())
                shirtPagerAdater.setClothes(it)
                scrollTo(if (prevSize==0) 0 else it.size, binding.pagerShirts)
            }
        })
        viewModel.allPantsList.observe(this, Observer {
            val prevSize=pantPagerAdapter.itemCount
            if(prevSize<it.size) {
                viewModel.setIsPantsAvailable(it.isNotEmpty())
                viewModel.updateFavouritePants()
                hideShowPants(it.isEmpty())
                pantPagerAdapter.setClothes(it)
                scrollTo(if (prevSize==0) 0 else it.size, binding.pagerPants)
            }
        })

        viewModel.isFavouriteCombo().observe(this, Observer {
            checkFav(it)
        })

        viewModel.randomPair.observe(this, Observer {
            binding.fabShuffle.isEnabled=true
            scrollTo(it.first, binding.pagerShirts)
            scrollTo(it.second, binding.pagerPants)
        })
    }

    private fun setListeners(){
        binding.fabShuffle.setOnClickListener {
            it.isEnabled=false
            viewModel.randomize()
        }

        binding.fabPants.setOnClickListener{
            startDialog(ClothingPagerAdapter.ClothType.Pants)
        }

        binding.fabShirts.setOnClickListener{
            startDialog(ClothingPagerAdapter.ClothType.Shirts)
        }

        binding.fabFavourite.setOnClickListener {
            val isFavourite=viewModel.setFavourite(binding.pagerShirts.currentItem,binding.pagerPants.currentItem)
            checkFav(isFavourite)
        }

        binding.pagerShirts.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.isShirtFavourite(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

        binding.pagerPants.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.isPantFavourite(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    private fun scrollTo(pos:Int, pager:ViewPager2){
        pager.setCurrentItem(pos,true)
    }

    private fun checkFav(isFav:Boolean){
        binding.fabFavourite.setImageResource(if (isFav) R.drawable.ic_favorite_32 else R.drawable.ic_favorite_border_32)
    }

    private fun hideShowShirts(isHide:Boolean){
        if(isHide){
            binding.pagerShirts.visibility= View.INVISIBLE
            binding.txtNoShirts.visibility=View.VISIBLE
        }
        else {
            binding.pagerShirts.visibility= View.VISIBLE
            binding.txtNoShirts.visibility=View.GONE
        }
    }

    private fun hideShowPants(isHide:Boolean){
        if(isHide){
            binding.txtNoPants.visibility=View.VISIBLE
            binding.pagerPants.visibility= View.INVISIBLE
        }
        else {
            binding.txtNoPants.visibility=View.GONE
            binding.pagerPants.visibility= View.VISIBLE
        }
    }

    private fun hideShowFab(isShow:Boolean){
        if(isShow){
            binding.fabFavourite.visibility= View.VISIBLE
            binding.fabShuffle.visibility= View.VISIBLE
        }
        else {
            binding.fabFavourite.visibility= View.GONE
            binding.fabShuffle.visibility= View.GONE
        }
    }

    private fun requestPermission(){
        val permission=ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if(permission==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf( Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_INTERNAL)
        }
        else{

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==REQUEST_CODE_READ_INTERNAL) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                val snack=Snackbar.make(binding.root, "App won't work properly!", Snackbar.LENGTH_INDEFINITE)
                snack.setAction("OK", View.OnClickListener { snack.dismiss() })
                snack.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode === RESULT_OK) {
            if(requestCode == RESULT_LOAD_IMG) {
                try {
                    if (data?.data != null) {
                        val imageUri = data.data as Uri
                        val type=this.type
                        val id=Calendar.getInstance().timeInMillis
                        val cloth = Cloth(
                            id,
                            type.toString(),
                            imageUri.toString()
                        )
                        viewModel.insert(cloth)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
            else { //camera data always null

            }
        }
    }

    private fun startDialog(type: ClothingPagerAdapter.ClothType) {
        this.type=type

//        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
//        myAlertDialog.setTitle("Upload Pictures Option")
//        myAlertDialog.setPositiveButton("Gallery",
//            DialogInterface.OnClickListener { arg0, arg1 ->
                var pictureActionIntent: Intent? = null
                pictureActionIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(
                    pictureActionIntent,
                    RESULT_LOAD_IMG
                )
//            })
//        myAlertDialog.setNegativeButton("Camera",
//            DialogInterface.OnClickListener { arg0, arg1 ->
//                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//                    // Ensure that there's a camera activity to handle the intent
//                    takePictureIntent.resolveActivity(packageManager)?.also {
//                        // Create the File where the photo should go
//                        val photoFile: File? = try {
//                            createImageFile()
//                        } catch (ex: IOException) {
//                            ex.printStackTrace()
//                            null
//                        }
//                        // Continue only if the File was successfully created
//                        photoFile?.also {
//                            val photoURI: Uri = FileProvider.getUriForFile(
//                                this,
//                                "com.gne.clothing.fileprovider",
//                                it
//                            )
//                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                            startActivityForResult(takePictureIntent, RESULT_CAMERA_IMG)
//                        }
//                    }
//                }
//
//            })
//        myAlertDialog.show()
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun addPicToGallery() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }
}