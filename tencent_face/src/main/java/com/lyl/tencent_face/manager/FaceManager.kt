package com.lyl.tencent_face.manager

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import com.lyl.tencent_face.bean.AuthResult
import com.lyl.tencent_face.bean.FaceForReg
import com.lyl.tencent_face.bean.Frame
import com.lyl.tencent_face.bean.FrameGroup
import com.lyl.tencent_face.utils.FloatsFileHelper
import com.lyl.tencent_face.utils.ImageConverter
import com.tencent.youtu.*
import com.tencent.youtu.YTFaceTracker.TrackedFace
import com.tencent.ytcommon.util.YTCommonInterface
import java.io.File
import java.util.*

/**
 *@author 梁亚龙
 * 日期： 2022年10月31日 15:16
 * 描述：
 */
class FaceManager {
    companion object {
        var authResult = AuthResult.getAuthResult(9999)

        /**
         * 人脸检索阈值：跟 模型版本/使用场景 相关
         */
        var FACE_RETRIEVE_THRESHOLD = 80.0f
        val URL = "https://license.youtu.qq.com/youtu/sdklicenseapi/license_generate"
        fun authWithDeviceSn(
            context: Context?,
            appId: String?,
            secretKey: String?
        ): AuthResult {
            System.loadLibrary("YTCommon")
            val code = YTCommonInterface.initAuth(
                context,
                URL,
                appId,
                secretKey,
                false
            )
            authResult = AuthResult.getAuthResult(code)
            return authResult
        }

        //是否开启距离和口罩识别
        var isQuality = true

        //距离识别
        var isDistance = true
        var distanceMax = 150
        var distanceMin = 40
        var isNoseBlock = true


        var isQualities = true

        /**
         * 补光灯是否开启
         */
        var isLightOn = false

        /**
         * 脸部正面角度评分阈值
         *
         *
         * 其中 scores[0] 为正面角度评分, 分数越高表示人脸越正面。<br></br>
         */
        const val FACE_QUALITY_SCORE_FACING_THRESHOLD = 0.7783f

        /**
         * 脸部可见度评分阈值
         *
         *
         * 其中 scores[1] 为脸部可见度评分, 分数越高表示越没有遮挡。 <br></br>
         */
        const val FACE_QUALITY_SCORE_VISIBILITY_THRESHOLD = 0.7001f

        /**
         * 清晰度评分阈值
         *
         *
         * 其中 scores[2] 为清晰度评分, 分数越高表示人脸越清晰。<br></br>
         */
        const val FACE_QUALITY_SCORE_SHARP_THRESHOLD = 0.4575f

        /**
         * 针对 **亮光** 场景的亮度评分阈值
         *
         *
         * 其中 scores[3] 为亮度评分, 分数越高表示越明亮<br></br>
         */
        const val FACE_QUALITY_SCORE_BRIGHT_ENV_THRESHOLD = 0.75f

        /**
         * 针对 **暗光** 场景的亮度评分阈值
         *
         *
         * 其中 scores[3] 为亮度评分, 分数越高表示越明亮<br></br>
         */
        //    public static final float FACE_QUALITY_SCORE_DARK_ENV_THRESHOLD = 0.3627f;
        const val FACE_QUALITY_SCORE_DARK_ENV_THRESHOLD = 0.3f
    }

    //人脸追踪
    var mYTFaceTracker: YTFaceTracker? = null //人脸追踪

    //人脸提取特征
    var mYTFaceFeature: YTFaceFeature? = null //人脸提取特征

    //检索
    var mYTFaceRetriever: YTFaceRetrieval? = null

    /**
     * 人脸遮挡, 表情, 瞳间距判断
     */
    var mYTFaceAlignment: YTFaceAlignment? = null

    var mYTFaceQualityPro: YTFaceQualityPro? = null
    var mFaceFile: String? = null
    val options = YTFaceTracker.Options()
    fun initFace(
        context: Context,
        appId: String?,
        secretKey: String?,
        faceFile: String
    ): AuthResult {
        var authWithDeviceSn = authWithDeviceSn(context, appId, secretKey)
        if (authWithDeviceSn.isSucceeded()) {
            loadLibs()
            loadModels(context.assets)
            initSdk(context)
            mFaceFile = faceFile

        }
        return authWithDeviceSn
    }


    /**
     * 加载人脸库
     */
    fun initFaceLib(): AuthResult {
        return addFace(getAllFace())
    }


    /**
     * 删除人脸
     *
     * @param fileName
     */
    fun deleteFace(fileName: String): AuthResult {
        if (!authResult.isSucceeded()) {
            return authResult
        }
        //删除文件
        val file = File(mFaceFile + fileName)
        return if (!file.delete()) {
            AuthResult.getAuthResult(9980)
        } else {
            //删除缓存
            val deleteFeatures = mYTFaceRetriever!!.deleteFeatures(arrayOf(fileName))
            if (deleteFeatures == 0) {
                AuthResult(deleteFeatures, "删除成功")
            } else {
                AuthResult(deleteFeatures, "删除失败")
            }

        }
    }

    /**
     * 获取所有人脸信息
     */
    fun getAllFace(): ArrayList<FaceForReg> {
        var filteredFaces: ArrayList<FaceForReg> = ArrayList()
        var file = File(mFaceFile)
        if (!file.exists()) {
            file.mkdirs()
        }
        for (listFile in file.listFiles()) {
            /** 人脸特征长度：512 维，取决于 YTFaceFeature 的版本 */
            val feat = FloatsFileHelper.readFloatsFromFile(
                listFile.absolutePath,
                YTFaceFeature.FEATURE_LENGTH
            )
            val f = FaceForReg(null, listFile.name, feat!!)
            if (f.name != null && f.name!!.isNotEmpty() && f.feature.isNotEmpty()
            ) {
                filteredFaces.add(f)
            }
        }
        return filteredFaces
    }

    /**
     * 图片增加人脸
     *
     * @param faceName
     * @param bmp
     */
    fun saveFace(faceName: String, bmp: Bitmap): AuthResult {
        if (!authResult.isSucceeded()) {
            return authResult
        }
        //彩图
        val colorFrame = Frame(ImageConverter.bitmap2RGB(bmp), bmp.width, bmp.height, 1)
        val frameGroup =
            FrameGroup(ImageConverter.rotateRgb888FrameIfNeeded(colorFrame), null, false)
        frameGroup.name = faceName
        //检测人脸
        val track = track(
            frameGroup.colorFrame!!,
            frameGroup.isContinuous
        )
        //检测人脸为空
        if (track.size == 0) {
            return AuthResult.getAuthResult(9981)
        }
        //选取最大一张人脸进行提取特征
        val feature = FloatArray(YTFaceFeature.FEATURE_LENGTH)
        val ret: Int = mYTFaceFeature!!.extract(
            getBiggestFace(track)!!.xy5Points,
            frameGroup.colorFrame!!.data,
            frameGroup.colorFrame!!.width,
            frameGroup.colorFrame!!.height,
            feature
        )
        return if (ret != 0) {
            AuthResult.getAuthResult(9981)
        } else saveFace(faceName, feature)
    }

    /**
     * 特征增加人脸
     *
     * @param faceName
     */
    fun saveFace(faceName: String, feature: FloatArray): AuthResult {
        val f = FaceForReg(null, faceName, feature)

        var addFace = addFace(f)
        if (addFace.isSucceeded()) {
            val filePath: String =
                File(mFaceFile + faceName).absolutePath
            FloatsFileHelper.writeFloatsToFile(feature, filePath)
            return AuthResult(0, "特征添加成功")
        }
        return addFace
    }

    /**
     * 添加人脸到人脸库List
     */
    fun addFace(filteredFaces: ArrayList<FaceForReg>): AuthResult {
        if (!authResult.isSucceeded()) {
            return authResult
        }
        var count = filteredFaces.size
        val allNames = arrayOfNulls<String>(count)
        val allFeatures = Array(count) { FloatArray(YTFaceFeature.FEATURE_LENGTH) }
        for (i in filteredFaces.indices) {
            allNames[i] = filteredFaces[i].name
            allFeatures[i] = filteredFaces[i].feature
        }
        val code: Int = mYTFaceRetriever!!.insertFeatures(allFeatures, allNames)
        return if (code == 0) {

            AuthResult(code, "初始化人脸库，共有${mYTFaceRetriever!!.queryFeatureNum()}人脸")
        } else AuthResult(code, "初始化人脸库失败")
    }

    /**
     * 添加人脸到人脸库
     */
    fun addFace(FaceForReg: FaceForReg): AuthResult {
        if (!authResult.isSucceeded()) {
            return authResult
        }
        val allNames = arrayOf(FaceForReg.name)
        val allFeatures = arrayOf(FaceForReg.feature)
        val code: Int = mYTFaceRetriever!!.insertFeatures(allFeatures, allNames)
        return if (code == 0) {

            AuthResult(code, "新增一个人脸，共有${mYTFaceRetriever!!.queryFeatureNum()}人脸")
        } else AuthResult(code, "人脸添加失败")
    }

    /**
     * 1：N查找人脸
     */
    fun findFace(frame: Frame, isContinuous: Boolean): AuthResult {
        if (!authResult.isSucceeded()) {
            return authResult
        }
        //检测人脸
        val track = track(
            frame,
            isContinuous
        )
        //检测人脸为空
        if (track.isEmpty()) {
            return AuthResult.getAuthResult(9981)
        }
        //选取最大一张人脸
        val biggestFace: TrackedFace = getBiggestFace(track)
        //质量检测
        if (isQuality) {
            var align = mYTFaceAlignment!!.align(
                frame.data,
                frame.width,
                frame.height,
                biggestFace.faceRect
            )
            if (align == null) {
                return AuthResult.getAuthResult(9971)
            } else {
                var faceStatus = mYTFaceAlignment!!.getStatus(align)
                if (isDistance) {
                    if (faceStatus.pupilDist < distanceMin) {
                        //人脸太远
                        return AuthResult.getAuthResult(9972)
                    } else if (faceStatus.pupilDist > distanceMax) {
                        //人脸太近
                        return AuthResult.getAuthResult(9973)
                    }
                }
                if (isNoseBlock) {
                    Log.i("TAG", "findFace: ${faceStatus.noseBlock}     ${faceStatus.mouthBlock}")
                    if (!(faceStatus.noseBlock <= 50 && faceStatus.mouthBlock <= 50)) {
                        //没有佩戴口罩
                        return AuthResult.getAuthResult(9974)
                    }

                }
            }

        }
        //质量检测（角度 遮挡，模糊，光线）
        if (isQualities) {
            var qualities = mYTFaceQualityPro!!.evaluate(
                biggestFace.xy5Points,
                frame.data,
                frame.width,
                frame.height
            )
            if (qualities.isEmpty()) {
                return AuthResult.getAuthResult(9961)
            }
            val facingScore = qualities[0] // 角度：分数越低，角度越大。

            val visibilityScore = qualities[1] // 遮挡：分数越低，遮挡程度越严重。

            val sharpScore = qualities[2] // 模糊：分数越低，模糊程度越严重。

            val brightnessScore = qualities[3] // 光线：分数越低，光线越暗，分数越高，光线越亮。
            // 注：由于不同维度的退化会存在相互影响，推荐使用优先级顺序为光线 -> 模糊 -> 角度 -> 遮挡。

            if (isLightOn) {
                //亮光环境下的亮度判断
                if (brightnessScore < FACE_QUALITY_SCORE_DARK_ENV_THRESHOLD) {
                    return AuthResult.getAuthResult(9962)
                }
            } else {
                //暗光环境下的亮度判断
                if (brightnessScore < FACE_QUALITY_SCORE_DARK_ENV_THRESHOLD) {
                    return AuthResult.getAuthResult(9962)
                }
            }
            if (sharpScore < FACE_QUALITY_SCORE_SHARP_THRESHOLD) {
                return AuthResult.getAuthResult(9963)
            }
            if (facingScore < FACE_QUALITY_SCORE_FACING_THRESHOLD) {

                return AuthResult.getAuthResult(9964)
            }
            if (!isNoseBlock) {
                if (visibilityScore < FACE_QUALITY_SCORE_VISIBILITY_THRESHOLD) {
                    return AuthResult.getAuthResult(9965)
                }
            }
        }


        //选取最大一张人脸进行提取特征
        val feature = FloatArray(YTFaceFeature.FEATURE_LENGTH)
        val ret: Int = mYTFaceFeature!!.extract(
            biggestFace.xy5Points,
            frame.data,
            frame.width,
            frame.height,
            feature
        )

        return if (ret != 0) {
            AuthResult.getAuthResult(9981)
        } else {
            var retrieve = mYTFaceRetriever!!.retrieve(feature, 1, FACE_RETRIEVE_THRESHOLD)
            if (retrieve.isNotEmpty()) {
                AuthResult(0, "识别成功", retrieve[0], biggestFace)
            } else {
                AuthResult(0, "识别成功", null, biggestFace)
            }
        }
    }

    /**
     * 检测人脸
     *
     * @param frame
     * @param isContinuous
     * @param tracker
     * @return
     */
    fun track(
        frame: Frame,
        isContinuous: Boolean,
    ): List<TrackedFace> {
        val trackedFaces: Array<TrackedFace?>
        val list: ArrayList<TrackedFace> = ArrayList<TrackedFace>()
        if (isContinuous) {
            trackedFaces = mYTFaceTracker!!.track(frame.data, frame.width, frame.height)
        } else {
            options.minFaceSize = 0 //如果检测不到人脸, 可以尝试调小这个值
            options.maxFaceSize = Math.min(frame.width, frame.height) //最大脸也就图那么大
            options.biggerFaceMode = true
            trackedFaces =
                mYTFaceTracker!!.detect(frame.data, frame.width, frame.height, options)
        }
        for (trackedFace in trackedFaces) {
            if (trackedFace != null) {
                list.add(trackedFace)
            }
        }
        return list
    }


    /**
     * 选出面积最大的脸
     */
    private fun getBiggestFace(faces: List<TrackedFace>): TrackedFace {
        var bigger: TrackedFace? = null
        if (faces.isNotEmpty()) {
            var maxArea = -1
            for (face in faces) {
                val faceRect = face.faceRect
                val area = faceRect.width() * faceRect.height()
                if (area > maxArea) {
                    maxArea = area
                    bigger = face
                }
            }
        }
        return bigger!!
    }

    /**
     * 加载 .so
     */
    fun loadLibs() {
        System.loadLibrary("YTUtils")
        System.loadLibrary("YTFaceFeature")
        System.loadLibrary("YTFaceRetrieval")
        System.loadLibrary("YTFaceQualityPro")
        System.loadLibrary("YTFaceTracker")
        System.loadLibrary("YTFaceAlignment")
    }

    fun loadModels(assetManager: AssetManager?) {
        YTFaceAlignment.globalInit(assetManager, "models/face-align-v6.3.0", "config.ini")
        YTFaceFeature.globalInit(assetManager, "models/face-feature-v704", "config.ini")
        YTFaceQualityPro.globalInit(assetManager, "models/face-quality-pro-v201", "config.ini")
        YTFaceTracker.globalInit(assetManager, "models/face-tracker-v5.3.5+v4.1.0", "config.ini")
    }

    /**
     * 初始化SDK
     *
     * @param context
     */
    private fun initSdk(context: Context) {
        val options = YTFaceTracker.Options()
        options.biggerFaceMode = true
        options.maxFaceSize = 999999
        options.minFaceSize = 100
        mYTFaceTracker = YTFaceTracker(options)
        mYTFaceFeature = YTFaceFeature()

        val cvtTable = YTFaceRetrieval.loadConvertTable(
            context.assets,
            "models/face-feature-v704/cvt_table_1vN_704.txt"
        )
        mYTFaceRetriever = YTFaceRetrieval(cvtTable, YTFaceFeature.FEATURE_LENGTH)
        mYTFaceAlignment = YTFaceAlignment()
        mYTFaceQualityPro = YTFaceQualityPro()
    }
}