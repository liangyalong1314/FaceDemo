package com.lyl.tencent_face.bean

import com.tencent.youtu.YTFaceTracker.TrackedFace

/**
 *@author 梁亚龙
 * 日期： 2022年10月31日 15:38
 * 描述：
 */
data class FaceForReg(
    var face: TrackedFace? = null,
    var name: String? = null,
    var feature: FloatArray
)