package com.lyl.tencent_face.utils

import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer

/**
 *@author 梁亚龙
 * 日期： 2022年10月31日 15:43
 * 描述：
 */
class FloatsFileHelper {
    companion object {
        fun writeFloatsToFile(floats: FloatArray, filePath: String?) {
            try {
                val file = RandomAccessFile(filePath, "rw")
                val channel = file.channel
                val bytesCount = 4 /*one float 4 bytes*/ * floats.size
                val buffer = ByteBuffer.allocate(bytesCount)
                buffer.clear()
                buffer.asFloatBuffer().put(floats)
                channel.write(buffer)
                buffer.rewind()
                channel.close()
                file.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun readFloatsFromFile(filePath: String?, floatsCount: Int): FloatArray? {
            val floats = FloatArray(floatsCount)
            try {
                val file = RandomAccessFile(filePath, "rw")
                val channel = file.channel
                val bytesCount = 4 /*one float 4 bytes*/ * floatsCount
                val buffer = ByteBuffer.allocate(bytesCount)
                buffer.clear()
                val readByteCount = channel.read(buffer)
                if (readByteCount != bytesCount) {
                    throw IOException(
                        String.format(
                            "readByteCount%s != bytesCount%s",
                            readByteCount,
                            bytesCount
                        )
                    )
                }
                buffer.rewind()
                buffer.asFloatBuffer()[floats]
                channel.close()
                file.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return floats
        }
    }
}