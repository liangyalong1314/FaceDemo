package com.lyl.tencent_face.bean

/**
 *@author 梁亚龙
 * 日期： 2022年10月31日 16:31
 * 描述：
 */
data class FrameGroup(
    var colorFrame: Frame? = null,

    var depthFrame: Frame? = null,
    var isContinuous: Boolean = false,
    /** 帧的名字, 例如用于表示图片文件名, 或者时间戳  */
    var name: String? = "",

)