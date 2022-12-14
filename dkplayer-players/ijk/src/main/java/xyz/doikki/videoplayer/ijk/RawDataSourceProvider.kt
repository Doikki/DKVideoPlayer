package xyz.doikki.videoplayer.ijk

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import tv.danmaku.ijk.media.player.misc.IMediaDataSource
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class RawDataSourceProvider(private var mDescriptor: AssetFileDescriptor?) : IMediaDataSource {
    private var mediaBytes: ByteArray? = null
    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        if (position + 1 >= mediaBytes!!.size) {
            return -1
        }
        var length: Int
        if (position + size < mediaBytes!!.size) {
            length = size
        } else {
            length = (mediaBytes!!.size - position).toInt()
            if (length > buffer.size) length = buffer.size
            length--
        }
        System.arraycopy(mediaBytes!!, position.toInt(), buffer, offset, length)
        return length
    }

    @Throws(IOException::class)
    override fun getSize(): Long {
        val length = mDescriptor!!.length
        if (mediaBytes == null) {
            val inputStream: InputStream = mDescriptor!!.createInputStream()
            mediaBytes = readBytes(inputStream)
        }
        return length
    }

    @Throws(IOException::class)
    override fun close() {
        if (mDescriptor != null) mDescriptor!!.close()
        mDescriptor = null
        mediaBytes = null
    }

    @Throws(IOException::class)
    private fun readBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    companion object {
        fun create(context: Context, uri: Uri?): RawDataSourceProvider? {
            try {
                val fileDescriptor = context.contentResolver.openAssetFileDescriptor(
                    uri!!, "r"
                )
                return RawDataSourceProvider(fileDescriptor)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return null
        }
    }
}