package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import org.jetbrains.skia.*
import org.laolittle.plugin.Fonts
import org.laolittle.plugin.toExternalResource
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.SkikoUtil.makeFromResource

object Osu : SimpleCommand(
    SeikiMain, "osu",
    description = "生成OSU风格的图标"
) {
    private val paintText = Paint().apply { color = Color.WHITE }
    private val osuImage = Image.makeFromResource("/Osu/logo.png")
    private val font = Fonts["Aller-Bold", 112.5F]
    /**
     * @author xiao_zheng
     * 自己写的 哼o(´^｀)o
     * 由LaoLittle改良Super版本（
     */
    @Handler
    suspend fun UserCommandSender.handle(text: String = "osu!") {
        val yPos: Float
        var osuText = TextLine.make(text, font)
        val textWidth = osuText.width

        if (textWidth <= 250) yPos = 137.5F + osuText.height / 2
        else {
            yPos = 210 - (textWidth - 255) / 20
            osuText = TextLine.make(text, font.makeWithSize(250F / textWidth * 112.5F))
        }

        Surface.makeRasterN32Premul(350, 350).apply {
            canvas.apply {
                drawImage(osuImage, 0F, 0F)
                drawTextLine(osuText, 175F - osuText.width / 2, yPos, paintText)
            }
        }.makeImageSnapshot().toExternalResource().use { it.sendAsImageTo(subject) }
    }
}