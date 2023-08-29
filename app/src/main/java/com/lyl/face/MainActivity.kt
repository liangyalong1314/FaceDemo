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

    }


    val imageAnalysis = ImageAnalysis.Builder()
        // enable the following line if RGBA output is needed.
         .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()


}