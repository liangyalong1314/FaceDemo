package com.lyl.tencent_face.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import com.lyl.tencent_face.bean.Frame
import com.tencent.youtu.YTUtils
import java.nio.ByteBuffer

/**
 *@author 梁亚龙
 * 日期： 2022年10月31日 16:24
 * 描述：
 */
class ImageConverter {
    companion object {
        fun bitmap2RGB(bitmap: Bitmap): ByteArray? {
            val bytes = bitmap.byteCount //返回可用于储存此位图像素的最小字节数
            val buffer = ByteBuffer.allocate(bytes) //  使用allocate()静态方法创建字节缓冲区
            bitmap.copyPixelsToBuffer(buffer) // 将位图的像素复制到指定的缓冲区
            val rgba = buffer.array()
            val pixels = ByteArray(rgba.size / 4 * 3)
            val count = rgba.size / 4

            //Bitmap像素点的色彩通道排列顺序是RGBA
            for (i in 0 until count) {
                pixels[i * 3] = rgba[i * 4] //R
                pixels[i * 3 + 1] = rgba[i * 4 + 1] //G
                pixels[i * 3 + 2] = rgba[i * 4 + 2] //B
            }
            return pixels
        }

        fun resizeBitmap(
            context: Context,
            id: Int,
            maxW: Int,
            maxH: Int,
            hasAlpha: Boolean
        ): Bitmap? {
            val resources = context.resources
            val options = BitmapFactory.Options()
            //需要拿得到系统处理的信息  比如解码出宽高,....
            options.inJustDecodeBounds = true
            //我们把原来的解码参数改了再去生成bitmap
            BitmapFactory.decodeResource(resources, id, options)
            //取到宽高
            val w = options.outWidth
            val h = options.outHeight
            //设置缩放系数
            options.inSampleSize = calcuteInSampleSize(w, h, maxW, maxH)
            if (!hasAlpha) {
                options.inPreferredConfig = Bitmap.Config.RGB_565
            }
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeResource(resources, id, options)
        }

        //返回结果是原来解码的图片的大小  是我们需要的大小的   最接近2的几次方倍
        private fun calcuteInSampleSize(w: Int, h: Int, maxW: Int, maxH: Int): Int {
            var inSampleSize = 1
            if (w > maxW && h > maxH) {
                inSampleSize = 2
                while (w / inSampleSize > maxW && h / inSampleSize > maxH) {
                    inSampleSize *= 2
                }
            }
            inSampleSize /= 2
            return inSampleSize
        }

        fun argbToBitmap(argb: ByteArray?, width: Int, height: Int): Bitmap? {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(argb))
            return bitmap
        }

        /**
         * TODO 性能优化
         */
        fun rgbToBitmap(rgb: ByteArray, width: Int, height: Int): Bitmap? {
            val colors = convertByteToColor(rgb) ?: return null //取RGB值转换为int数组
            val bitmap = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
            ) //这样  bitmap.isMutable() == true
            bitmap.setPixels(colors, 0, width, 0, 0, width, height)
            return bitmap
            // return Bitmap.createBitmap(colors, 0, width, width, height, Config.ARGB_8888);//这样 bitmap.isMutable() == false
        }

        /** 将纯RGB数据数组转化成int像素数组  */
        private fun convertByteToColor(rgb: ByteArray): IntArray? {
            val size = rgb.size
            if (size == 0) {
                return null
            }
            var arg = 0
            if (size % 3 != 0) {
                arg = 1
            }
            // 一般RGB字节数组的长度应该是3的倍数，
            // 不排除有特殊情况，多余的RGB数据用黑色0XFF000000填充
            val color = IntArray(size / 3 + arg)
            var red: Int
            var green: Int
            var blue: Int
            val colorLen = color.size
            if (arg == 0) {
                for (i in 0 until colorLen) {
                    red = convertByteToInt(rgb[i * 3])
                    green = convertByteToInt(rgb[i * 3 + 1])
                    blue = convertByteToInt(rgb[i * 3 + 2])
                    // 获取RGB分量值通过按位或生成int的像素值
                    color[i] = red shl 16 or (green shl 8) or blue or -0x1000000
                }
            } else {
                for (i in 0 until colorLen - 1) {
                    red = convertByteToInt(rgb[i * 3])
                    green = convertByteToInt(rgb[i * 3 + 1])
                    blue = convertByteToInt(rgb[i * 3 + 2])
                    color[i] = red shl 16 or (green shl 8) or blue or -0x1000000
                }
                color[colorLen - 1] = -0x1000000
            }
            return color
        }

        /** 将byte数当成无符号的变量去转化成int  */
        private fun convertByteToInt(data: Byte): Int {
            val heightBit = (data.toInt() shr 4 and 0x0F)
            return heightBit * 16 + (0x0F and data.toInt())
        }

        fun toBitmap(image: Image): Bitmap {
            val planes: Array<Image.Plane> = image.planes
            val buffer: ByteBuffer = planes[0].buffer
            val pixelStride: Int = planes[0].pixelStride
            val rowStride: Int = planes[0].rowStride
            val rowPadding: Int = rowStride - pixelStride * image.width
            val bitmap = Bitmap.createBitmap(
                image.width + rowPadding / pixelStride,
                image.height, Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)
            return bitmap
        }

        /**
         * 图片处理
         *
         * @param rgb888Frame
         * @return
         */
        fun rotateRgb888FrameIfNeeded(rgb888Frame: Frame): Frame {
            if (rgb888Frame.exifOrientation === 1) {
                return rgb888Frame
            }
            val ytImage = YTUtils.rotateRGB888(
                rgb888Frame.data,
                rgb888Frame.width,
                rgb888Frame.height,
                rgb888Frame.exifOrientation
            )
            return Frame(ytImage.data, ytImage.width, ytImage.height, 1)
        }
    }
}