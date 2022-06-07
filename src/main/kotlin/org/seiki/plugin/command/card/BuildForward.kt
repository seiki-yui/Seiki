package org.seiki.plugin.command.card

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.events.UserMessageEvent
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.sendTo
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.getOrWait

object BuildForward : SimpleCommand(
    SeikiMain, "bf",
    description = "构造一个聊天记录"
) {
    @Handler
    suspend fun CommandSenderOnMessage<UserMessageEvent>.handle(user: User) {
        sendMessage("请在30秒内发送消息...")
        fromEvent.getOrWait()?.let {
            buildForwardMessage(subject!!) {
                add(user, it)
            }.sendTo(subject!!)
        }
    }
}