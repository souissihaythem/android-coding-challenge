package com.kenkou.photorecognitionkenkou.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import java.util.*

object PermissionsUtils {

    fun checkAndRequestPermissions(activity: Activity): Boolean {
        val camerapermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        val writepermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)


        val listPermissionsNeeded = ArrayList<String>()

        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toTypedArray(), Constants.REQUESTS.REQUEST_PERMISSIONS)
            return false
        }
        return true
    }
}
