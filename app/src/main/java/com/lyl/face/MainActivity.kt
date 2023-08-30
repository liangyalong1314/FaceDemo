package com.lyl.face

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.camera.view.PreviewView

import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.lyl.face.databinding.ActivityMainBinding

import com.lyl.tencent_face.bean.Frame
import com.lyl.tencent_face.camera.MyCameraFilter
import com.lyl.tencent_face.camera.ProcessCamera2Provider
import com.lyl.tencent_face.camera.ProcessCameraProvider
import com.lyl.tencent_face.manager.FaceManager
import com.lyl.tencent_face.utils.ImageConverter
import java.nio.ByteBuffer
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private var preview: Preview? = null

    private var face = FaceManager()
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FileUtil.init(this)
        var authWithDeviceSn =
            face.initFace(this, "p8rzuoxtj", "4b9da57aae624850b2d2c20b11f476ae", FileUtil . getSdCardCacheDir ()!!.absolutePath
                .toString() + "/fubaoFace/")
        Log.i("TAG", "onCreate: "+authWithDeviceSn)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO 省略了权限申请，具体看文章中 "前置操作" 部分


        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), ImageAnalysis.Analyzer { imageProxy ->
            var toBitmap = ImageConverter.toBitmap(imageProxy.image!!)
            var frame =
                Frame(ImageConverter.bitmap2RGB(toBitmap), toBitmap.width, toBitmap.height, 1)
            Log.i("TAG", "onCreate: "+face.findFace(frame,true))
            imageProxy.close()
        })
        setUpCamera(binding.previewView2)
    }

    private fun setUpCamera(previewView: PreviewView) {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                var cameraProvider = cameraProviderFuture.get()
                preview = Preview.Builder().build()
                var cameraSelector : CameraSelector = CameraSelector.Builder()
                    .addCameraFilter(MyCameraFilter(0))
                    .build()
                preview?.setSurfaceProvider(binding.previewView.surfaceProvider)
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector, preview
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))



        val cameraProviderFuture2: ListenableFuture<ProcessCamera2Provider> =
            ProcessCamera2Provider.getInstance(this)
        cameraProviderFuture2.addListener({
            try {
                var cameraProvider = cameraProviderFuture2.get()
                preview = Preview.Builder().build()
                var cameraSelector : CameraSelector = CameraSelector.Builder()
                    .addCameraFilter(MyCameraFilter(1))
                    .build()
                preview?.setSurfaceProvider(binding.previewView2.surfaceProvider)
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector, preview,imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))

    }



    val imageAnalysis = ImageAnalysis.Builder()
        // enable the following line if RGBA output is needed.
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()


}