package org.seiki.plugin

import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.LightApp
import net.mamoe.mirai.utils.info
import org.seiki.SweetBoy
import org.seiki.SweetBoy.matchRegexOrFail
import org.seiki.SweetBoy.transToTime
import org.seiki.plugin.command.audio.Audio
import org.seiki.plugin.command.audio.Say
import org.seiki.plugin.command.card.BiliApp
import org.seiki.plugin.command.card.BiliLight
import org.seiki.plugin.command.card.bili
import org.seiki.plugin.command.image.*
import org.seiki.plugin.command.plain.*

object SeikiMain : KotlinPlugin(
    JvmPluginDescription(
        id = "org.seiki.main",
        version = "1.0-SNAPSHOT",
        name = "Seiki Main"
    )
) {
    val audioFolder = dataFolder.resolve("audio")
    val resFolder = dataFolder.resolve("res")

    private lateinit var jobTimeTick: Job
    override fun onEnable() {
        logger.info { "Seiki Main loaded" }
        val commandList: List<Command> = listOf(
            Ping, Gpbt, Form, Two, Dazs, Kfc, Diana, Yiyan, Nbnhhsh, Baike, Bottle, Fencing,
            Diu, Tian, Bishi, Pa, Zan, Love, Qian, Draw,
            Osu, PronHub, FiveK, BlackWhite, Zero,
            Setu, Cosplay,
            Audio, Say
        )
        commandList.forEach {
            CommandManager.registerCommand(it)
        }
        val eventChannel = globalEventChannel()
        eventChannel.subscribeAlways<MessageEvent> {
            message.forEach {
                if (it is LightApp) {
                    val gson = Gson()
                    runCatching {
                        val json = gson.fromJson(it.content, BiliLight::class.java)
                        if (json.meta.detail_1.appid == "1109937557") {
                            val url = SweetBoy.get(
                                json.meta.detail_1.qqdocurl.substringBefore('?')
                            ).request.url.toString()
                            val bv = url.matchRegexOrFail(biliUrlRegex)[1]
                            subject.sendMessage(subject.bili(bv))
                        }
                    }.onFailure { logger.info { "不是B站小程序" } }
                    kotlin.runCatching {
                        val json = gson.fromJson(it.content, BiliApp::class.java)
                        if (json.meta.news.appid == 1105517988) {
                            val url = SweetBoy.get(json.meta.news.jumpUrl).request.url.toString()
                            val bv = url.matchRegexOrFail(biliUrlRegex)[1]
                            subject.sendMessage(subject.bili(bv))
                        }
                    }.onFailure { logger.info { "不是B站类XML的JSON卡片" } }
                }
            }
        }
        eventChannel.subscribeMessages {
            biliUrlRegex findingReply {
                val (id) = it.destructured
                subject.bili(id)
            }
            bili23tvRegex findingReply {
                val (url) = it.destructured
                subject.bili(SweetBoy.get(url).request.url.toString().matchRegexOrFail(biliUrlRegex)[1])
            }

            "error" reply { throw Exception("www") }
        }
        eventChannel.subscribeAlways<BotOnlineEvent> {
            jobTimeTick = launch {
                while (true) {
                    TimeTickEvent(bot, System.currentTimeMillis()).broadcast()
                    delay(999L)
                }
            }
        } // bot上线
        eventChannel.subscribeAlways<BotInvitedJoinGroupRequestEvent> {
            if (invitorId in ownerList) launch {
                delay(1000L)
                accept()
            }
        } // bot拉群
        eventChannel.subscribeAlways<BotJoinGroupEvent> {
            launch {
                delay(100L)
                group.sendMessage("这里是Seiki Bot.发送\"# help\"来康教程.")
            }
        } // bot进群
        eventChannel.subscribeAlways<NudgeEvent> {
            if (target.id == bot.id && from.id != bot.id) launch {
                delay(100L)
                from.nudge().sendTo(subject)
            }
        } // 戳一戳
        eventChannel.subscribeAlways<MemberMuteEvent> {
            launch {
                delay(100L)
                group.sendMessage(
                    if (operator == null) "已口球${member.nameCardOrNick}(${member.id})"
                    else "${member.nameCardOrNick}(${member.id})被${operatorOrBot.nameCardOrNick}(${operatorOrBot.id})塞了口球"
                )
            }
        } // 禁言
        eventChannel.subscribeAlways<MemberUnmuteEvent> {
            launch {
                delay(100L)
                group.sendMessage(
                    if (operator == null) "已解禁${member.nameCardOrNick}(${member.id})"
                    else "${member.nameCardOrNick}(${member.id})的口球被${operatorOrBot.nameCardOrNick}(${operatorOrBot.id})取了下来"
                )
            }
        } // 解禁
        eventChannel.subscribeAlways<MemberJoinEvent.Active> {
            launch {
                delay(100L)
                group.sendMessage("欢迎${member.nameCardOrNick}(${member.id})的加入")
            }
        } // 进群
        eventChannel.subscribeAlways<MemberJoinEvent.Invite> {
            launch {
                delay(100L)
                group.sendMessage("${invitor.nameCardOrNick}(${invitor.id})邀请${member.nameCardOrNick}(${member.id})加入了本群")
            }
        } // 拉群
        eventChannel.subscribeAlways<MemberLeaveEvent.Quit> {
            launch {
                delay(100L)
                group.sendMessage("${member.nameCardOrNick}(${member.id})逃跑了")
            }
        } // 退群
        eventChannel.subscribeAlways<MemberLeaveEvent.Kick> {
            launch {
                delay(100L)
                group.sendMessage(
                    if (operator == null) "已飞机${member.nameCardOrNick}(${member.id})"
                    else "${operatorOrBot.nameCardOrNick}(${operatorOrBot.id})飞机了${member.nameCardOrNick}(${member.id})"
                )
            }
        } // 踢群
        eventChannel.subscribeAlways<MemberPermissionChangeEvent> {
            launch {
                delay(100L)
                group.sendMessage("${member.nameCardOrNick}(${member.id})从${origin.getName()}便乘了${new.getName()}")
            }
        } // 管理权限改变
        eventChannel.subscribeAlways<TimeTickEvent> {
            if (this.timestamp.transToTime("HH:mm:ss") == "11:45:13") {
                bot.groups.forEach {
                    it.sendMessage("Seiki报时!\n现在是11:45:14")
                }
            }
        }
    }

    override fun onDisable() {
        jobTimeTick.cancel()
        super.onDisable()
    }
//    TODO("Logger覆写")
//    override fun PluginComponentStorage.onLoad() {
//        this.contributeBotConfigurationAlterer { botId, config ->
//            config.apply {
//                botLoggerSupplier = {
//                    object : MiraiLogger by MiraiLogger.Factory.create(Bot::class) {
//                        override fun error(message: String?, e: Throwable?) {
//                            e?.printStackTrace()
//                            Bot.findInstance(botId)?.let {
//                                launch {
//                                    it.getGroup(813089162L)?.sendMessage(message ?: "unknown")
//                                }
//                            }
//                            error(message)
//                        }
//
//                        override fun error(message: String?) {
//                            Bot.findInstance(botId)?.let {
//                                launch {
//                                    it.getGroup(813089162L)?.sendMessage(message ?: "unknown")
//                                }
//                            }
//                        }
//
//                        override val isVerboseEnabled: Boolean get() = true
//                        override val isInfoEnabled: Boolean get() = true
//                        override val isWarningEnabled: Boolean get() = true
//                        override val isDebugEnabled: Boolean get() = true
//                    }
//                }
//            }
//        }
//    }
}