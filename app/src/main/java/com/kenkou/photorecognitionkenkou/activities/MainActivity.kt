package com.kenkou.photorecognitionkenkou.activities

import android.accounts.Account
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.kenkou.photorecognitionkenkou.BuildConfig
import com.kenkou.photorecognitionkenkou.R
import com.kenkou.photorecognitionkenkou.models.FeatureType
import com.kenkou.photorecognitionkenkou.models.Image
import com.kenkou.photorecognitionkenkou.models.ImageContent
import com.kenkou.photorecognitionkenkou.models.RequestImage
import com.kenkou.photorecognitionkenkou.utils.Constants.HEADER_KEY.AUTHORIZATION
import com.kenkou.photorecognitionkenkou.utils.Constants.HEADER_KEY.CONTENT_TYPE
import com.kenkou.photorecognitionkenkou.utils.Constants.HEADER_VALUE.APPLICATION_JSON
import com.kenkou.photorecognitionkenkou.utils.Constants.HEADER_VALUE.BEARER
import com.kenkou.photorecognitionkenkou.utils.Constants.REQUESTS.REQUEST_CAMERA
import com.kenkou.photorecognitionkenkou.utils.Constants.REQUESTS.REQUEST_GALLERY
import com.kenkou.photorecognitionkenkou.utils.Constants.REQUESTS.REQUEST_SIGNIN
import com.kenkou.photorecognitionkenkou.utils.Constants.SCOPES.CLOUD_PLATEFORM
import com.kenkou.photorecognitionkenkou.utils.Constants.SCOPES.CLOUD_VISION
import com.kenkou.photorecognitionkenkou.utils.Constants.SCOPES.OAUTH2_PROFILE_EMAIL
import com.kenkou.photorecognitionkenkou.utils.ImageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException


class MainActivity : BaseActivity() {

    private var encodedImage: String? = null

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

        if (requestCode == REQUEST_GALLERY && data != null) {
            val contentURI = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val byteArrayImage = baos.toByteArray()
                encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
                signIn()

                Toast.makeText(this@MainActivity, R.string.image_saved, Toast.LENGTH_SHORT).show()
                imageView.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, R.string.failed, Toast.LENGTH_SHORT).show()
            }

        } else if (requestCode == REQUEST_CAMERA && data != null) {
            val thumbnail = data.extras.get("data") as Bitmap

            val baos = ByteArrayOutputStream()
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteArrayImage = baos.toByteArray()
            encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
            signIn()

            imageView.setImageBitmap(thumbnail)
            ImageUtils.saveImage(thumbnail, this)
            Toast.makeText(this@MainActivity, R.string.image_saved, Toast.LENGTH_SHORT).show()
        } else if (requestCode == REQUEST_SIGNIN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun getImageDescription(token: String) {
        val imageContent = ImageContent(encodedImage)
        val featureType = FeatureType("LABEL_DETECTION")
        val features = ArrayList<FeatureType>()
        features.add(featureType)
        val image = Image(imageContent, features)
        val images = ArrayList<Image>()
        images.add(image)
        val requestImage = RequestImage(images)

        val map = HashMap<String, String>()
        map[CONTENT_TYPE] = APPLICATION_JSON
        map[AUTHORIZATION] = "$BEARER $token"

        addDisposable(appService.getImage(map, BuildConfig.API_KEY, requestImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    //if (loadingBar != null) loadingBar.start()
                }
                .subscribe(
                        { result ->
                            var suggestions = ""
                            if (result.responses?.size != 0) {
                                val imagesResults = result.responses!![0].labelAnnotations
                                var counter = 1
                                for (item in imagesResults) {
                                    suggestions = suggestions + counter + "- " + item.description + "\n"
                                    counter++
                                }
                            }
                            progressbar.visibility = View.GONE
                            resultTextView.text = suggestions
                        },
                        { error ->
                            progressbar.visibility = View.GONE
                            resultTextView.text = error.message
                        }
                ))
    }

    private fun signIn() {
        // TODO: get access_token only when access_token expire (use refresh_token)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(CLOUD_PLATEFORM))
                .requestScopes(Scope(CLOUD_VISION))
                .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_SIGNIN)
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            RetrieveTokenTask().execute(account.account)
        } catch (e: ApiException) {
        }

    }

    private inner class RetrieveTokenTask : AsyncTask<Account, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressbar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Account): String? {
            val accountName = params[0]
            return GoogleAuthUtil.getToken(applicationContext, accountName, OAUTH2_PROFILE_EMAIL)
        }

        override fun onPostExecute(token: String) {
            super.onPostExecute(token)
            getImageDescription(token)
        }
    }

}