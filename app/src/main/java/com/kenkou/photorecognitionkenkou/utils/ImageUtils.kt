package com.kenkou.photorecognitionkenkou.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object ImageUtils {


    fun saveImage(myBitmap: Bitmap, context: Context): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + Constants.DIRECTORY.IMAGE)
        if (!wallpaperDirectory.exists()) {

            wallpaperDirectory.mkdirs()
        }

        try {
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(context,
                    arrayOf(f.path),
                    arrayOf("image/jpeg"), null)
            fo.close()

            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }


    fun choosePhotoFromGallary(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        activity.startActivityForResult(galleryIntent, Constants.REQUESTS.REQUEST_GALLERY)
    }


    fun takePhotoFromCamera(activity: Activity) {
        if (PermissionsUtils.checkAndRequestPermissions(activity)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activity.startActivityForResult(intent, Constants.REQUESTS.REQUEST_CAMERA)
        }
    }
}
