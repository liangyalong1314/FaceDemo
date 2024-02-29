package com.lyl.face
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import com.lyl.face.databinding.ActivityMainBinding
import com.lyl.tencent_face.manager.FaceManager



class MainActivity : AppCompatActivity() {

    private var preview: Preview? = null

    private var face = FaceManager()
    private lateinit var binding: ActivityMainBinding
    var state=true
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




    }


    val imageAnalysis = ImageAnalysis.Builder()
        // enable the following line if RGBA output is needed.
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
    val imageAnalysis2 = ImageAnalysis.Builder()
        // enable the following line if RGBA output is needed.
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

}
