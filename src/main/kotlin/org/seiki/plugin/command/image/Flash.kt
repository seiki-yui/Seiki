package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.event.events.UserMessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import org.jetbrains.skia.*
import org.laolittle.plugin.toExternalResource
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.getOrWaitImage
import org.seiki.plugin.runCatching
import kotlin.math.min
import org.jetbrains.skia.Image.Companion as SkImage

object Flash : SimpleCommand(
    SeikiMain, "flash",
    description = "生成假闪图"
) {
    @Handler
    suspend fun CommandSenderOnMessage<UserMessageEvent>.handle(image: Image? = null) {
        subject!!.runCatching {
            val img = image ?: fromEvent.getOrWaitImage() ?: return@runCatching

            val input = SkImage.makeFromEncoded(SweetBoy.getBytes(img.queryUrl()))

            val b = Bitmap.makeFromImage(input)
            b.mosaic(30)

            val w = input.width
            val h = input.height

            val foo = min(w, h) * .30f
            val bar = foo * .5f
            Surface.makeRasterN32Premul(w, h).apply {
                writePixels(b, 0, 0)
                canvas.apply {
                    drawPaint(Paint().apply {
                        alpha = 160
                    })

                    translate(w * .5f - bar, h * .5f - bar)

                    drawPath(Path().apply {
                        val x1 = foo * .65f

                        val p1 = Point(bar * .85f, bar * .85f)
                        val p4 = Point(bar * 1.15f, bar * 1.15f)
                        val p2 = Point(p4.x, p1.y)
                        val p3 = Point(p1.x, p4.y)
                        moveTo(x1, 0f)
                        lineTo(foo * .2f, p3.y)
                        lineTo(p3)
                        moveTo(foo - x1, foo)
                        lineTo(foo * .8f, p1.y)
                        lineTo(p2)
                        moveTo(p2)
                        lineTo(p1)
                        lineTo(p3)
                        moveTo(p3)
                        lineTo(p4)
                        lineTo(p2)
                    }, Paint().apply {
                        color = Color.WHITE
                        alpha = 160
                        pathEffect = PathEffect.makeCorner(foo / 10)
                    })
                }
            }.makeImageSnapshot().toExternalResource().use { it.sendAsImageTo(subject!!) }
        }
    }

    private fun Bitmap.mosaic(size: Int) {
        val w = width
        val h = height

        for (y in 0 until h step size) for (x in 0 until w step size) {
            val color = getColor(x, y)
            erase(color, IRect.makeXYWH(x, y, size, size))
        }
    }
}