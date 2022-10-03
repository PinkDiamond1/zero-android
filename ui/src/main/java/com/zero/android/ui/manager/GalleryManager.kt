package com.zero.android.ui.manager

import android.app.Activity
import android.content.Intent
import com.github.dhaval2404.imagepicker.ImagePicker

object GalleryManager {

	fun getCameraImagePicker(activity: Activity, onImagePicker: (Intent) -> Unit) {
		ImagePicker.with(activity).apply {
			cameraOnly()
			createIntent { onImagePicker(it) }
		}
	}

	fun getGalleryImagePicker(activity: Activity, onImagePicker: (Intent) -> Unit) {
		ImagePicker.with(activity).apply {
			galleryOnly()
			createIntent { onImagePicker(it) }
		}
	}

	fun getChatMediaPicker(activity: Activity, fromCamera: Boolean, onImagePicker: (Intent) -> Unit) {
		ImagePicker.with(activity).apply {
			if (fromCamera) cameraOnly() else galleryOnly()
			galleryMimeTypes(arrayOf("image/png", "image/jpg", "image/jpeg", "video/mp4"))
			createIntent { onImagePicker(it) }
		}
	}
}
