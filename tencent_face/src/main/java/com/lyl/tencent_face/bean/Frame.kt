package com.lyl.tencent_face.bean

/**
 *@author 梁亚龙
 * 日期： 2022年10月31日 16:21
 * 描述：
 */
data class Frame(
    var data: ByteArray?,
    var width: Int ,
    var height: Int ,
    var exifOrientation: Int,
    var enqueueTime: Long = System.currentTimeMillis(),

    )
