package com.zhangke.framework.utils

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.Region
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat

public open class DrawableWrapper(drawable: Drawable? = null) : Drawable(), Drawable.Callback {

    private var wrappedDrawable: Drawable? = null

    init {
        setWrappedDrawable(drawable)
    }

    override fun draw(canvas: Canvas) {
        wrappedDrawable?.draw(canvas)
    }

    override fun onBoundsChange(bounds: Rect) {
        wrappedDrawable?.bounds = bounds
    }

    override fun setChangingConfigurations(configs: Int) {
        wrappedDrawable?.changingConfigurations = configs
    }

    override fun getChangingConfigurations(): Int {
        return wrappedDrawable?.changingConfigurations ?: super.getChangingConfigurations()
    }

    @Suppress("deprecation")
    override fun setDither(dither: Boolean) {
        wrappedDrawable?.setDither(dither)
    }

    override fun setFilterBitmap(filter: Boolean) {
        wrappedDrawable?.isFilterBitmap = filter
    }

    override fun setAlpha(alpha: Int) {
        wrappedDrawable?.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        wrappedDrawable?.colorFilter = colorFilter
    }

    override fun isStateful(): Boolean {
        return wrappedDrawable?.isStateful ?: false
    }

    override fun setState(stateSet: IntArray): Boolean {
        return wrappedDrawable?.setState(stateSet) ?: false
    }

    override fun getState(): IntArray {
        return wrappedDrawable?.state ?: super.getState()
    }

    override fun jumpToCurrentState() {
        wrappedDrawable?.jumpToCurrentState()
    }

    override fun getCurrent(): Drawable {
        return wrappedDrawable ?: this
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        var v = super.setVisible(visible, restart)
        wrappedDrawable?.setVisible(visible, restart)?.let { v = it }
        return v
    }

    @Suppress("deprecation")
    override fun getOpacity(): Int {
        return wrappedDrawable?.opacity ?: PixelFormat.UNKNOWN
    }

    override fun getTransparentRegion(): Region? {
        return wrappedDrawable?.transparentRegion
    }

    override fun getIntrinsicWidth(): Int {
        return wrappedDrawable?.intrinsicWidth ?: -1
    }

    override fun getIntrinsicHeight(): Int {
        return wrappedDrawable?.intrinsicHeight ?: -1
    }

    override fun getMinimumWidth(): Int {
        return wrappedDrawable?.minimumWidth ?: super.getMinimumWidth()
    }

    override fun getMinimumHeight(): Int {
        return wrappedDrawable?.minimumHeight ?: super.getMinimumHeight()
    }

    override fun getPadding(padding: Rect): Boolean {
        return wrappedDrawable?.getPadding(padding) ?: super.getPadding(padding)
    }

    override fun invalidateDrawable(who: Drawable) {
        invalidateSelf()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        scheduleSelf(what, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        unscheduleSelf(what)
    }

    override fun onLevelChange(level: Int): Boolean {
        return wrappedDrawable?.setLevel(level) ?: false
    }

    override fun setAutoMirrored(mirrored: Boolean) {
        wrappedDrawable?.let { DrawableCompat.setAutoMirrored(it, mirrored) }
    }

    override fun isAutoMirrored(): Boolean {
        return wrappedDrawable?.let { DrawableCompat.isAutoMirrored(it) } ?: false
    }

    override fun setTint(tint: Int) {
        wrappedDrawable?.let { DrawableCompat.setTint(it, tint) }
    }

    override fun setTintList(tint: ColorStateList?) {
        wrappedDrawable?.let { DrawableCompat.setTintList(it, tint) }
    }

    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        tintMode ?: return
        wrappedDrawable?.let { DrawableCompat.setTintMode(it, tintMode) }
    }

    override fun setHotspot(x: Float, y: Float) {
        wrappedDrawable?.let { DrawableCompat.setHotspot(it, x, y) }
    }

    override fun setHotspotBounds(left: Int, top: Int, right: Int, bottom: Int) {
        wrappedDrawable?.let { DrawableCompat.setHotspotBounds(it, left, top, right, bottom) }
    }

    public fun getWrappedDrawable(): Drawable? {
        return wrappedDrawable
    }

    public fun setWrappedDrawable(drawable: Drawable?) {
        wrappedDrawable?.callback = null
        wrappedDrawable = drawable
        drawable?.callback = this
    }
}
