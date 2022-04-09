package org.seiki.plugin.command.plain

import net.mamoe.mirai.console.command.MemberCommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.Member
import org.seiki.plugin.SeikiMain

object Fencing : SimpleCommand(
    SeikiMain, "fencing",
    description = "击剑 at一个人，随机禁言其中一个"
) {
    @Handler
    suspend fun MemberCommandSender.handle(member: Member) {
        arrayOf(this.user, member).random().mute((1..3).random() * 60)
    }
}