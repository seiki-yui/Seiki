package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import org.jetbrains.skia.*
import org.laolittle.plugin.Fonts
import org.laolittle.plugin.toExternalResource
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import kotlin.math.min

object Zero : SimpleCommand(
    SeikiMain, "0", "0%",
    description = "生成0%加载图片"
) {
    @Handler
    suspend fun UserCommandSender.handle(image: Image? = null) {
        if (image == null) return

        val skikoImage =
            org.jetbrains.skia.Image.makeFromEncoded(SweetBoy.downloadAsByteStream(image.queryUrl()).readBytes())
        val w21 = (skikoImage.width shr 1).toFloat()
        val h21 = (skikoImage.height shr 1).toFloat()
        val radius = min(w21, h21) * .24f

        val text = TextLine.make("0%", Fonts["MiSans-Regular.ttf", radius * .6f])
        Surface.makeRaster(skikoImage.imageInfo).apply {
            val paint = Paint().apply {
                isAntiAlias = true
                color = Color.WHITE
            }
            canvas.apply {
                clear(Color.BLACK)
                drawImage(skikoImage, 0F, 0F, paint.apply {
                    alpha = 160
                })
                drawCircle(w21, h21, radius, paint.apply {
                    mode = PaintMode.STROKE
                    strokeWidth = radius * .19f
                    maskFilter = MaskFilter.makeBlur(FilterBlurMode.SOLID, radius * .2f)
                })
                drawTextLine(text, w21 - text.width / 2, h21 + text.height / 4, paint.apply {
                    mode = PaintMode.FILL
                    maskFilter = null
                })
            }

            makeImageSnapshot().toExternalResource().sendAsImageTo(subject)
        }
    }
}