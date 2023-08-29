package com.lyl.tencent_face.bean

import android.graphics.Bitmap
import com.tencent.youtu.YTFaceRetrieval
import com.tencent.youtu.YTFaceTracker

/**
 *@author 梁亚龙
 * 日期： 2022年10月31日 14:32
 * 描述：
 */
data class AuthResult(
    var code: Int,
    var msg: String,
    var retrievedItem: YTFaceRetrieval.RetrievedItem? = null,
    var trackedFace: YTFaceTracker.TrackedFace? = null
) {
    fun isSucceeded(): Boolean = code == 0

    companion object {
        fun getAuthResult(code: Int) =
            when (code) {
                0 -> AuthResult(code, "授权成功")
                1 -> AuthResult(code, "无效授权, 请检查授权文件的文件名/路径是否正确，比如license和licence的拼写方式是否统一")
                2 -> AuthResult(
                    code,
                    "packageName 不匹配, 请检查您的 App 使用的 packageName 和申请的是否一致, 检查是否使用了正确的授权文件"
                )
                16 -> AuthResult(code, "授权文件已过期, 请检查系统时间, 或者续期")
                -1 -> AuthResult(code, "授权文件路径错误, 请检查文件名/路径")
                -10 -> AuthResult(code, "授权文件路径错误, 请检查文件名/路径，或检查initType参数（0或2）")
                -11 -> AuthResult(code, "授权文件路径错误, 请检查文件名/路径，或检查initType参数（0或2）")
                1002 -> AuthResult(code, "网络连接失败, 请检查是否正常联网、是否ping通initAuth中的授权服务器URL: ")
                1003 -> AuthResult(code, "网络初始化错误, 服务器URL: ")
                1045 -> AuthResult(code, "授权设备已达上限，请申请续期后再使用")
                2002 -> AuthResult(code, "证书下载失败, 建议重装APP重新激活")
                2003 -> AuthResult(code, "证书保存失败, 建议重装APP重新激活")
                2004 -> AuthResult(code, "没有写权限, 检查APP读写权限")
                3003 -> AuthResult(code, "证书文件为空, 建议重装APP重新激活")
                3004 -> AuthResult(
                    code,
                    "授权文件解析失败，可能是授权文件损坏，或者授权文件版本不匹配, 检查授权文件是否正确，或者重新申请新版本的授权文件"
                )
                3005 -> AuthResult(code, "证书解析出错, 建议重装APP重新激活")
                3006 -> AuthResult(code, "证书解析出错, 建议重装APP重新激活")
                3007 -> AuthResult(code, "序列号为空, 设备生产厂商务必填写唯一的序列号")
                3008 -> AuthResult(code, "序列号解析错误, 设备生产厂商务必填写唯一的序列号")
                3013 -> AuthResult(code, "设备信息不匹配, 确认是否在平台上添加了序列号, 重装APP重新激活")
                3014 -> AuthResult(code, "设备信息不匹配, 确认是否在平台上添加了序列号, 重装APP重新激活")
                3015 -> AuthResult(code, "package name不匹配, 检查项目的package name")
                3016 -> AuthResult(code, "package name为空, 建议重新申请授权")
                3017 -> AuthResult(code, "证书已过期（累积时间）, 请续期")
                3018 -> AuthResult(code, "证书已过期, 请续期")
                3019 -> AuthResult(code, "license版本不匹配, 请更新common库或license")
                3022 -> AuthResult(code, "设备信息不匹配, 建议重装APP重新激活")
                4001 -> AuthResult(code, "设备序列号无效, 设备生产厂商务必填写唯一的序列号")
                4003 -> AuthResult(code, "没有权限获取序列号, 检查READ_PHONE_STATE权限")
                1001 -> AuthResult(code, "请求字段中参数错误, 检查APPID是否正确")
                -1005 -> AuthResult(code, "设备时间和服务器不符, 请确认设备时间正确")
                -1104 -> AuthResult(code, "设备序列号不匹配, 检查在平台登记的序列号和设备实际序列号是否一致")
                -1301 -> AuthResult(code, "序列号信息为空, 备生产厂商务必填写唯一的序列号")
                -1302 -> AuthResult(code, "没有查询到序列号记录, 确认是否在平台上添加了序列号")
                -1401 -> AuthResult(code, "该设备续期次数超过限制, 换一台设备重新申请授权，或申请解禁")
                -1402 -> AuthResult(code, "授权时间无效, 请续期")
                -1405 -> AuthResult(code, "appid没有匹配到设备, 检查授权代码中的appid是否正确, 检查账号下是否正确添加了序列号")
                -1407 -> AuthResult(code, "授权已过期")

                9999 -> AuthResult(code, "未初始化")
                9980 -> AuthResult(code, "人脸删除失败")
                9981 -> AuthResult(code, "检测人脸为空")

                9971 -> AuthResult(code, "质量识别不足")
                9972 -> AuthResult(code, "距离太远")
                9973 -> AuthResult(code, "距离太近")
                9974 -> AuthResult(code, "请佩戴口罩")

                9961 -> AuthResult(code, "达不到质量识别")
                9962 -> AuthResult(code, "亮度不足")
                9963 -> AuthResult(code, "人脸清晰度不足")
                9964 -> AuthResult(code, "请正视屏幕")
                9965 -> AuthResult(code, "请勿遮挡脸部")
                else -> AuthResult(code, "未知错误")
            }


    }
}
