package com.kenkou.photorecognitionkenkou.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.kenkou.photorecognitionkenkou.BuildConfig
import com.kenkou.photorecognitionkenkou.R
import com.kenkou.photorecognitionkenkou.models.FeatureType
import com.kenkou.photorecognitionkenkou.models.Image
import com.kenkou.photorecognitionkenkou.models.ImageContent
import com.kenkou.photorecognitionkenkou.models.RequestImage
import com.kenkou.photorecognitionkenkou.services.AppServiceApi
import com.kenkou.photorecognitionkenkou.utils.ImageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    val appService by lazy {
        AppServiceApi.create()
    }

    private var disposables: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectImageButton.setOnClickListener { showPictureDialog() }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle(R.string.select_action)
        val pictureDialogItems = arrayOf(getString(R.string.select_photo_from_galery), getString(R.string.capture_photo_from_camera))
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> ImageUtils.choosePhotoFromGallary(this@MainActivity)
                1 -> ImageUtils.takePhotoFromCamera(this@MainActivity)
            }
        }
        pictureDialog.show()
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ImageUtils.REQUEST_GALLERY && data != null) {
            val contentURI = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val byteArrayImage = baos.toByteArray()
                val encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                getImageDescription(encodedImage)

                Toast.makeText(this@MainActivity, R.string.image_saved, Toast.LENGTH_SHORT).show()
                imageView.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, R.string.failed, Toast.LENGTH_SHORT).show()
            }

        } else if (requestCode == ImageUtils.REQUEST_CAMERA && data != null) {
            val thumbnail = data.extras.get("data") as Bitmap

            val baos = ByteArrayOutputStream()
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteArrayImage = baos.toByteArray()
            val encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
            getImageDescription(encodedImage)

            imageView.setImageBitmap(thumbnail)
            ImageUtils.saveImage(thumbnail, this)
            Toast.makeText(this@MainActivity, R.string.image_saved, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageDescription(encodedImage: String) {
        val imageContent = ImageContent(encodedImage)
        val featureType = FeatureType("LABEL_DETECTION")
        val features = ArrayList<FeatureType>()
        features.add(featureType)
        val image = Image(imageContent, features)
        val images = ArrayList<Image>()
        images.add(image)
        val requestImage=  RequestImage(images)

        addDisposable(appService.getImage(BuildConfig.API_KEY, requestImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    //if (loadingBar != null) loadingBar.start()
                }
                .subscribe(
                        {
                            result ->
                            var suggestions =""
                            if (result.responses?.size != 0){
                                val imagesResults = result.responses!![0].labelAnnotations
                                var counter = 1
                                for (item in imagesResults){
                                    suggestions = suggestions + counter + "- " + item.description + "\n"
                                    counter++
                                }
                            }
                            resultTextView.text = suggestions
                        },
                        {
                            error ->
                            resultTextView.text = error.message
                        }
                ))
    }


    private fun addDisposable(disposable: Disposable) {
        disposables?.add(disposable)
    }

    private fun clearDisposable() {
        disposables?.dispose()
        disposables?.clear()
    }

    override fun onStop() {
        super.onStop()

        clearDisposable()
    }
}