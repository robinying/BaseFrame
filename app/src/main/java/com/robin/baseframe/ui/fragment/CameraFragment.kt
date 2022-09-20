package com.robin.baseframe.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.SoundEffectConstants
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.ext.contentResolver
import com.robin.baseframe.app.ext.nav
import com.robin.baseframe.databinding.FragmentCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : BaseFragment<BaseViewModel, FragmentCameraBinding>() {
    private lateinit var cameraExecutor: ExecutorService
    private var mCamera: Camera? = null
    private lateinit var mPreview: Preview
    private var mImageCapture: ImageCapture? = null
    private var mVideoCapture: VideoCapture? = null
    private var bitmap: Bitmap? = null
    private var isTakePhoto = false
    override fun initView(savedInstanceState: Bundle?) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                mActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        binding.viewFinder.setOnTouchListener { v, event ->
            val action =
                FocusMeteringAction.Builder(binding.viewFinder.meteringPointFactory.createPoint(event.x, event.y))
                    .build()
            showTapView(event.x.toInt(), event.y.toInt())
            mCamera?.cameraControl?.startFocusAndMetering(action)
            return@setOnTouchListener true
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                nav().navigateUp()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            mPreview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            bindPreview(cameraProvider, true, false)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("RestrictedApi")
    private fun bindPreview(cameraProvider: ProcessCameraProvider, isBack: Boolean, isVideo: Boolean) {
        mImageCapture = ImageCapture.Builder()
            .setTargetRotation(binding.viewFinder.display.rotation)
            .build()
        mVideoCapture = VideoCapture.Builder()
            .setTargetRotation(binding.viewFinder.display.rotation)
            .setVideoFrameRate(25)
            .setBitRate(3 * 1024 * 1024)
            .build()
        //切换前后摄像头
        val cameraSelector = if (isBack) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
        try {
            cameraProvider.unbindAll()
            mCamera = if(isVideo){
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, mPreview, mVideoCapture
                )
            }else{
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, mPreview, mImageCapture
                )
            }

        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun takenPictureInternal() {
        val contentValues = ContentValues()
        contentValues.put(
            MediaStore.MediaColumns.DISPLAY_NAME, "camera_capture"
                    + "_" + System.currentTimeMillis()
        )
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()

        // Mirror image
        ImageCapture.Metadata().apply {
            isReversedHorizontal = true
        }

        mImageCapture?.takePicture(outputFileOptions, cameraExecutor, MyCaptureCallback(mActivity))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun showTapView(x: Int, y: Int) {
        val popupWindow = PopupWindow(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val imageView = ImageView(mActivity)
        imageView.setImageResource(R.drawable.ic_focus_view)
        popupWindow.setContentView(imageView);
        popupWindow.showAsDropDown(binding.viewFinder, x, y)
        binding.viewFinder.postDelayed(popupWindow::dismiss, 600)
        binding.viewFinder.playSoundEffect(SoundEffectConstants.CLICK)
    }

    class MyCaptureCallback(
        private val context: Context
    ) : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val uri = outputFileResults.savedUri

            val path = if (uri != null) (" @ " + uri.path) else "none"

            Toast.makeText(
                context, "Picture got:$path.", Toast.LENGTH_SHORT
            ).show()
        }

        override fun onError(exception: ImageCaptureException) {
            Log.d("cameraX", "onError:${exception.imageCaptureError}")
        }
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    }
}