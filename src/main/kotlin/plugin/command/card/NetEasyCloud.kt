package org.seiki.plugin.command.card

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.utils.MiraiExperimentalApi
import org.seiki.plugin.SeikiMain

object NetEasyCloud : SimpleCommand (
    SeikiMain, "wy",
    description = "网易云音乐"
) {
    @OptIn(MiraiExperimentalApi::class)
    @Handler
    suspend fun CommandSender.handle(text: String) {
        
    }
}