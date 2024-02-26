package com.zhangke.framework.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import java.io.File
import java.io.FileOutputStream

object BitmapUtils {

    fun buildBitmapWithText(
        width: Int,
        height: Int,
        text: String,
        backgroundColor: Int,
    ): Bitmap {
        val paint = Paint()
        paint.textSize = width * 0.6F
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.isAntiAlias = true
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(backgroundColor)
        canvas.drawText(text, width / 2F, height / 2F, paint)
        return bitmap
    }

    fun saveToFile(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, it)
        }
    }
}
