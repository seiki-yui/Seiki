package org.seiki.plugin.command.file

import cn.hutool.core.util.URLUtil
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import net.mamoe.mirai.console.command.MemberCommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain
import org.seiki.plugin.runCatching

object Upload : SimpleCommand(
    SeikiMain, "upload", "上传",
    description = "向Seiki资源站上传文件."
) {
    @Handler
    suspend fun MemberCommandSender.handle(name: String) {
        subject.runCatching {
            val path = if (System.getProperties().getProperty("os.name").startsWith("Windows"))
                "E:\$name" else "/www/wwwroot/8008/files/$name"
            runCatching {
                val url = subject.files.root.files().filter { it.isFile && (it.name == name) }.first().getUrl()!!
                SweetBoy.getFile(url, path)
            }.onFailure {
                when (it) {
                    is java.lang.NullPointerException -> subject.sendMessage("名为\"${name}\"的文件不存在!")
                    else -> throw it
                }
            }.onSuccess {
                subject.sendMessage("已上传至 http://res.seiki.fun/files/${URLUtil.encode(name)}")
            }
        }
    }
}