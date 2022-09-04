@file:Suppress("KDocUnresolvedReference")

package org.seiki.plugin.command.image

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.event.events.UserMessageEvent
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.sendTo
import org.seiki.SweetBoy
import org.seiki.plugin.MirageUtils
import org.seiki.plugin.MirageUtils.convertToJPG
import org.seiki.plugin.MirageUtils.touchDir
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.getOrWaitImage
import org.seiki.plugin.uploadAsImage
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Echoosx
 * @see [Github页面](https://github.com/Echoosx/MiraiMirage)
 * */
object Mirage : SimpleCommand(
    SeikiMain, "mirage", "tank","幻影坦克",
    description = "生成幻影坦克图"
) {
    private val INPUT_PATH = "${SeikiMain.dataFolder.absolutePath}/Mirage/Input"
    private val OUTPUT_PATH = "${SeikiMain.dataFolder.absolutePath}/Mirage/Output"
    @Handler
    suspend fun CommandSenderOnMessage<UserMessageEvent>.handle() {
        val timestamp = DateTimeFormatter.ofPattern("YYMMddHHmmss").format(LocalDateTime.now())
        val outsideImage = this.fromEvent.getOrWaitImage("幻影坦克开始制作...请发送表图...") ?: return
        touchDir("${INPUT_PATH}/${user!!.id}")
        SweetBoy.getFile(outsideImage.queryUrl(),"${INPUT_PATH}/${user!!.id}/${timestamp}_out.jpg")
        if(outsideImage.imageType.name != "JPG") convertToJPG("${INPUT_PATH}/${user!!.id}/${timestamp}_out.jpg")
        val insideImage = this.fromEvent.getOrWaitImage("请发送里图...") ?: return
        SweetBoy.getFile(insideImage.queryUrl(),"${INPUT_PATH}/${user!!.id}/${timestamp}_in.jpg")
        if(insideImage.imageType.name != "JPG") convertToJPG("${INPUT_PATH}/${user!!.id}/${timestamp}_in.jpg")
        touchDir("${OUTPUT_PATH}/${user!!.id}")
        MirageUtils.buildMirageTank(
            "${INPUT_PATH}/${user!!.id}/${timestamp}_out.jpg",
            "${INPUT_PATH}/${user!!.id}/${timestamp}_in.jpg",
            "${OUTPUT_PATH}/${user!!.id}/${timestamp}.png"
        )
        subject!!.uploadAsImage(File("${OUTPUT_PATH}/${user!!.id}/${timestamp}.png")).sendTo(subject!!)
    }
}