package ca.ulaval.ima.mp.api

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.core.app.ActivityCompat.startActivityForResult
import ca.ulaval.ima.mp.MiniProject

object UploadService {

    private val PICK_IMAGE = 1

    /**
     * Start a gallery selection intent with tag PICK_IMAGE
     *
     *  @param activity : Activity catching the selection result
     */
    fun choose(activity: Activity) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            activity, Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE, null
        )
    }

    fun extractBitmapFromResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): Bitmap? {
        if (resultCode != Activity.RESULT_OK)
            return null
        when (requestCode) {
            PICK_IMAGE -> {
                val imageUri = data?.data
                return MediaStore.Images.Media.getBitmap(
                    MiniProject.appContext?.contentResolver,
                    imageUri
                )
            }
        }
        return null
    }

}