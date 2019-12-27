package org.wit.hillfortapp.helpers

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private var mCurrentPhotoPath: String? = null
val REQUEST_CAMERA_PERMISSIONS_REQUEST_CODE = 35


fun showImagePicker(parent: Activity, id: Int) {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_OPEN_DOCUMENT
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    val chooser = Intent.createChooser(intent, "placeholder")
    parent.startActivityForResult(chooser, id)
}

fun readImage(activity: Activity, resultCode: Int, data: Intent?): Bitmap? {
    var bitmap: Bitmap? = null
    if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, data.data)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return bitmap
}

fun readImageFromPath(context: Context, path: String): Bitmap? {
    var bitmap: Bitmap? = null
    val uri = Uri.parse(path)
    if (uri != null) {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
        } catch (e: Exception) {
        }
    }
    if (bitmap != null) {
        if (bitmap.height > 2000 || bitmap.width > 2000) {
            println("RESIZING")
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, false)
        }
    }
    return bitmap
}

// Source: http://www.kotlincodes.com/kotlin/camera-intent-with-kotlin-android/
fun takePicture(parent: Activity, id: Int) {

    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val file: File = createFile(parent)

    val uri: Uri = FileProvider.getUriForFile(
        parent,
        "com.example.android.fileprovider",
        file
    )
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
    parent.startActivityForResult(intent, id)
}

@Throws(IOException::class)
private fun createFile(parent: Activity): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File = parent.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = absolutePath
    }
}

fun checkCameraAndStoragePermissions(activity: Activity): Boolean {
    return if (ActivityCompat.checkSelfPermission(
            activity,
            CAMERA
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            activity,
            READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        true
    } else {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(READ_EXTERNAL_STORAGE, CAMERA),
            REQUEST_CAMERA_PERMISSIONS_REQUEST_CODE
        )
        false
    }
}

fun getCurrentImagePath(): String? {
    return mCurrentPhotoPath
}

