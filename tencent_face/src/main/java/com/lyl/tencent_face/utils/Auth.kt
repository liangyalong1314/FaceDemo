package com.lyl.tencent_face.utils

import android.content.Context
import com.lyl.tencent_face.bean.AuthResult
import com.tencent.ytcommon.util.YTCommonInterface

/**
 *@author 梁亚龙
 * 日期： 2022年10月31日 14:28
 * 描述：
 */
class Auth {
    companion object {
        var authResult = AuthResult.getAuthResult(9999)
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
    }



}