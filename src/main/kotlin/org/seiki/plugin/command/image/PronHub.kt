@file:Suppress("KDocUnresolvedReference")

package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import org.jetbrains.skia.*
import org.laolittle.plugin.Fonts
import org.laolittle.plugin.toExternalResource
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching

/**
 * @author 滑稽360
 * @see [Github页面](https://github.com/cssxsh)
 * */
object PronHub : SimpleCommand(
    SeikiMain, "ph", "pronhub",
    description = "生成PronHub风格的图标"
) {
    @Handler
    suspend fun UserCommandSender.handle(left: String = "Pron", right: String = "Hub") {
        subject.runCatching {
            val phHeight = 170
            val widthPlus = 12
            val leftText = TextLine.make(left, Fonts["MiSans-Bold.ttf"])
            val leftPorn = Surface.makeRasterN32Premul(leftText.width.toInt() + (widthPlus shl 1), phHeight)
            leftPorn.canvas.apply {
                clear(Color.makeARGB(255, 0, 0, 0))
                drawTextLine(
                    leftText,
                    (leftPorn.width - leftText.width) / 2 + 5,
                    ((leftPorn.height shr 1) + (leftText.height / 4)),
                    Paint().apply { color = Color.makeARGB(255, 255, 255, 255) }
                )
            }
            val rightText = TextLine.make(right, Fonts["MiSans-Bold.ttf"])
            val rightPorn = Surface.makeRasterN32Premul(
                rightText.width.toInt() + (widthPlus shl 1) + 20,
                rightText.height.toInt()
            )
            rightPorn.canvas.apply {
                val rRect = RRect.makeComplexXYWH(
                    ((rightPorn.width - rightText.width) / 2) - widthPlus,
                    0F,
                    rightText.width + widthPlus,
                    rightText.height - 1,
                    floatArrayOf(19.5F)
                )
                drawRRect(
                    rRect, Paint().apply { color = Color.makeARGB(255, 255, 145, 0) }
                )
                // clear(Color.makeARGB(255, 255,144,0))
                // drawCircle(100F, 100F, 50F, Paint().apply { color = Color.BLUE })
                drawTextLine(
                    rightText,
                    ((rightPorn.width - rightText.width - widthPlus.toFloat()) / 2),
                    ((rightPorn.height shr 1) + (rightText.height / 4) + 2),
                    Paint().apply { color = Color.makeARGB(255, 0, 0, 0) }
                )
            }
            val surface = Surface.makeRasterN32Premul(leftPorn.width + rightPorn.width, phHeight)
            surface.apply {
                canvas.apply {
                    clear(Color.makeARGB(255, 0, 0, 0))
                    drawImage(leftPorn.makeImageSnapshot(), 0F, 0F)
                    drawImage(
                        rightPorn.makeImageSnapshot(),
                        leftPorn.width.toFloat() - (widthPlus shr 1),
                        (((phHeight - rightPorn.height) shr 1) - 2).toFloat()
                    )
                }
            }
            surface.makeImageSnapshot().toExternalResource().sendAsImageTo(subject)
        }
    }
}