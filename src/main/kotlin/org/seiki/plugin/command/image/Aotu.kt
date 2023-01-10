package org.seiki.plugin.command.image

import com.google.gson.Gson
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.utils.debug
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.warning
import org.seiki.SweetBoy
import org.seiki.plugin.runCatching
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.uploadAsImage

object Aotu : SimpleCommand(
    SeikiMain, "aotu", "好图",
    description = "获取几张好康的"
) {
    @Handler
    suspend fun UserCommandSender.handle(number: Int = 1) {
        if (number in 1..10) {
            SeikiMain.logger.info { "Aotu - 开始缓存" }
            repeat(number) { now ->
                runCatching {
                    val list = listOf(
                        "https://www.dmoe.cc/random.php",
//                        "https://api.ixiaowai.cn/api/api.php",太慢
                        "https://api.yimian.xyz/img?type=moe",
                        "https://img.xjh.me/random_img.php?return=302",
//                        "https://api.ayao.ltd/images/api.php",不稳定
                        "https://api.hanximeng.com/ranimg/api.php",
//                        "https://air.moe/ranimg/api.php",
//                        "https://api.r10086.com/img-api.php?type=动漫综合2",太慢
//                        "https://cdn.seovx.com/d/?mom=302",质量不咋地
//                        "https://api.ucany.net/acg-pc.php"
//                        "https://api.btstu.cn/sjbz/?lx=dongman"太慢
//                        "https://api.paugram.com/wallpaper/?source=jsd"
                    )
                    val url = list.random()
                    SeikiMain.logger.debug { url }
                    subject.uploadAsImage(url).sendTo(subject)
                }.onSuccess {
                    SeikiMain.logger.info { "Aotu - ${now + 1} / $number SUCCESS" }
                }.onFailure {
                    SeikiMain.logger.warning { "Aotu - ${now + 1} / $number FAILURE" }
                }
            }
        }
    }
}