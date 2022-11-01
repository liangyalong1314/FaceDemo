package com.lyl.face

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.lyl.face.databinding.ActivityMainBinding
import com.lyl.tencent_face.bean.Frame
import com.lyl.tencent_face.manager.FaceManager
import com.lyl.tencent_face.utils.ImageConverter
import java.nio.ByteBuffer
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var cameraProvider: ProcessCameraProvider
    private var preview: Preview? = null
    private var camera: Camera? = null
    private var face = FaceManager()
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FileUtil.init(this)
        var authWithDeviceSn =
            face.initFace(this, "", "", FileUtil . getSdCardCacheDir ()!!.absolutePath
                .toString() + "/fubaoFace/")
        Log.i("TAG", "onCreate: "+authWithDeviceSn)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO 省略了权限申请，具体看文章中 "前置操作" 部分

        setUpCamera(binding.previewView)
        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), ImageAnalysis.Analyzer { imageProxy ->
            var toBitmap = ImageConverter.toBitmap(imageProxy.image!!)
            var frame =
                Frame(ImageConverter.bitmap2RGB(toBitmap), toBitmap.width, toBitmap.height, 1)
            Log.i("TAG", "onCreate: "+face.findFace(frame,true))
            imageProxy.close()
        })

    }
    private fun setUpCamera(previewView: PreviewView) {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider, previewView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(
        cameraProvider: ProcessCameraProvider,
        previewView: PreviewView
    ) {
        //解除所有绑定，防止CameraProvider重复绑定到Lifecycle发生异常
        cameraProvider.unbindAll()
        preview = Preview.Builder().build()
        camera = cameraProvider.bindToLifecycle(
            this,
            CameraSelector.DEFAULT_BACK_CAMERA, preview,imageAnalysis
        )
        preview?.setSurfaceProvider(previewView.surfaceProvider)
    }

    val imageAnalysis = ImageAnalysis.Builder()
        // enable the following line if RGBA output is needed.
         .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .setTargetResolution(Size(1280, 720))
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()


}