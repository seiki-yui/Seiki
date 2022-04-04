package org.seiki.plugin.command.image

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import org.jetbrains.skia.*
import org.laolittle.plugin.Fonts
import org.laolittle.plugin.toExternalResource
import org.seiki.plugin.SeikiMain
import java.net.URL

object BlackWhite : SimpleCommand(
    SeikiMain, "bw",
    description = "生成黑白图"
) {
    @Handler
    suspend fun CommandSender.handle(content: String = "", image: Image) {
        val skikoImage = withContext(Dispatchers.IO) {
            URL(image.queryUrl()).openStream().use { input ->
                requireNotNull(input)
                org.jetbrains.skia.Image.makeFromEncoded(input.readBytes())
            }
        }
        val paint = Paint().apply {
            isAntiAlias = true
        }
        val h = skikoImage.height
        val w = skikoImage.width
        val foo = h / 6
        val bar = foo / (1.4f)
        val fontSize = if (bar.toInt() * content.length > w) ((w * 0.8f) / content.length) else bar
        val text = TextLine.make(content, Fonts["MiSans-Bold.ttf", fontSize])
        val surface = Surface.makeRasterN32Premul(skikoImage.width, h + (foo * 1.4f).toInt())
        surface.canvas.apply {
            clear(Color.BLACK)
            drawImage(skikoImage, 0F, 0F, paint.apply {
                colorFilter = ColorFilter.makeMatrix(
                    ColorMatrix(
                        0.33F, 0.38F, 0.29F, 0F, 0F,
                        0.33F, 0.38F, 0.29F, 0F, 0F,
                        0.33F, 0.38F, 0.29F, 0F, 0F,
                        0.33F, 0.38F, 0.29F, 1F, 0F,
                    )
                )
            })
            drawTextLine(text,
                ((surface.width - text.width) / 2),
                h + ((foo + text.height) / 2),
                paint.apply { color = Color.WHITE })
        }
        surface.makeImageSnapshot().toExternalResource().sendAsImageTo(subject!!)
    }
}