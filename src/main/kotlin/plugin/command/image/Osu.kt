package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import org.jetbrains.skia.Color
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface
import org.jetbrains.skia.TextLine
import org.laolittle.plugin.Fonts
import org.laolittle.plugin.toExternalResource
import org.seiki.plugin.SeikiMain

object Osu : SimpleCommand(
    SeikiMain, "osu",
    description = "生成OSU风格的图标"
) {
    private val res: ByteArray = SeikiMain::class.java.getResourceAsStream("/osuLogo.png")!!.use { it.readAllBytes() }
    /**
     * @author xiao_zheng
     * 自己写的 哼o(´^｀)o
     */
    @Handler
    suspend fun UserCommandSender.handle(text: String = "osu!") {
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
        }
        val image =
            org.jetbrains.skia.Image.makeFromEncoded(res)
        var osuText = TextLine.make(text, Fonts["Aller-Bold.ttf", 112.5F])
        var yPos = 137.5F + osuText.height / 2
        if (osuText.width > 250) {
            yPos = 210 - (osuText.width - 255) / 20
            osuText = TextLine.make(text, Fonts["Aller-Bold.ttf", 250F / osuText.width * 112.5F])
        }
        val surface = Surface.makeRasterN32Premul(350, 350)
        surface.apply {
            canvas.apply {
                drawImage(image, 0F, 0F)
                drawTextLine(osuText, 175F - osuText.width / 2, yPos, paint)
            }
        }
        surface.makeImageSnapshot().toExternalResource().use { it.sendAsImageTo(subject) }
    }
}