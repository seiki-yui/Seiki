package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.event.events.UserMessageEvent
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import org.jetbrains.skia.Bitmap
import org.seiki.plugin.SeikiMain
import org.jetbrains.skia.IRect
import org.laolittle.plugin.toExternalResource
import org.seiki.SweetBoy
import org.jetbrains.skia.Image as SkiaImage
import org.seiki.plugin.MathUtil
import org.seiki.plugin.MathUtil.bilinearInterpolate
import org.seiki.plugin.getOrWaitImage
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

object Marble: SimpleCommand(
    SeikiMain, "marble"
) {
    @Handler
    suspend fun CommandSenderOnMessage<UserMessageEvent>.handle(i: Float? = null, x: Float? = null,y: Float? = null) {
        val image = SkiaImage.makeFromEncoded(SweetBoy.getBytes((this.fromEvent.getOrWaitImage() ?: return).queryUrl()))
        val foo = image.width * .1f
        marble(image, MarbleFilter(x ?: foo, y ?: (foo * .1f), i ?: 1f)).use { bitmap ->
            SkiaImage.makeFromBitmap(bitmap).toExternalResource()
        }.sendAsImageTo(subject!!)
    }

    private fun marble(image: SkiaImage, marble: MarbleFilter = MarbleFilter()): Bitmap {
        //val surface = Surface.makeRaster(image.imageInfo)
        val src = Bitmap.makeFromImage(image)

        val dst = Bitmap().apply {
            allocPixels(image.imageInfo)
        }

        val h = src.height
        val w = src.width

        val h1 = h - 1
        val w1 = w - 1

        val out = FloatArray(2)

        for (y in 0 until h) for (x in 0 until w) {
            marble.transformInverse(x, y, out)
            val srcX = floor(out[0]).toInt()
            val srcY = floor(out[1]).toInt()

            val xWeight = out[0] - srcX
            val yWeight = out[1] - srcY

            val nw = src.pixel(srcX, srcY, w1, h1)
            val ne = src.pixel(srcX + 1, srcY, w1, h1)
            val sw = src.pixel(srcX, srcY + 1, w1, h1)
            val se = src.pixel(srcX + 1, srcY + 1, w1, h1)

            val pixel = bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se)

            dst.erase(pixel, IRect.makeXYWH(x, y, 1, 1))
        }

        src.close()

        return dst
    }

    private fun Bitmap.pixel(x: Int, y: Int, w1: Int, h1: Int): Int {
        val clampX = x.coerceIn(0, w1)
        val clampY = y.coerceIn(0, h1)

        return getColor(clampX, clampY)
    }

    private const val TWO_PI = PI * 2

    class MarbleFilter(
        private val xScale: Float = 4f,
        private val yScale: Float = 4f,
        private val turbulence: Float = 1f
    ) {
        private val sinTable = FloatArray(256)
        private val cosTable = FloatArray(256)

        private fun displacementMap(x: Int, y: Int): Int {
            return clamp((127 * (1 + MathUtil.Noise.noise2(x / xScale, y / xScale))).toInt())
        }

        fun transformInverse(x: Int, y: Int, out: FloatArray) {
            val displacement = displacementMap(x, y)
            out[0] = x + sinTable[displacement]
            out[1] = y + cosTable[displacement]
        }

        init {
            repeat(256) {
                val angle = (TWO_PI * it / 256f) * turbulence
                sinTable[it] = (-yScale * sin(angle)).toFloat()
                cosTable[it] = (yScale * cos(angle)).toFloat()
            }
        }

        companion object {
            fun clamp(value: Int) = value.coerceIn(0, 255)
        }
    }
}