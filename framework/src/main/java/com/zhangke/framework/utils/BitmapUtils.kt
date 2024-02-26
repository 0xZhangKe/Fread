package com.zhangke.framework.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
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
        val textSize = if (text.length == 1) {
            width * 0.6F
        } else {
            width * 0.8F / text.length
        }
        paint.textSize = textSize
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.isAntiAlias = true
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(backgroundColor)
        val bundle = Rect()
        paint.getTextBounds(text, 0, text.length, bundle)
        val y = height / 2F + bundle.height() / 2F - bundle.bottom
        canvas.drawText(text, width / 2F, y, paint)
        return bitmap
    }

    fun saveToFile(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, it)
        }
    }
}
