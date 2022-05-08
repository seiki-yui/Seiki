@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.seiki.plugin

import com.huaban.analysis.jieba.JiebaSegmenter
import com.huaban.analysis.jieba.WordDictionary
import kotlinx.coroutines.TimeoutCancellationException
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.AudioSupported
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.nextMessage
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.seiki.SweetBoy
import org.seiki.plugin.SeikiMain.tempFolder
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import java.awt.Image as AwtImage

class TimeTickEvent(
    var bot: Bot,
    var timestamp: Long,
) : AbstractEvent()

val ownerList = arrayListOf(2630557998L, 1812691029L)

val biliUrlRegex =
    """[\s\S]*(?:(?:https?://)?(?:www\.)?bilibili\.com/video/)?([aA][vV]\d+|[bB][vV][a-zA-Z0-9]+)[\s\S]*""".toRegex()
val bili23tvRegex = """[\s\S]*((?:https?://)?(?:www\.)?b23\.tv/[a-zA-Z0-9]+)[\s\S]*""".toRegex()
val biliUserRegex = """[\s\S]*(?:https?://)?(?:space\.bilibili\.com|bilibili\.com/space)/(\d+)[\s\S]*""".toRegex()

fun MemberPermission.getName(): String = when (this) {
    MemberPermission.MEMBER -> "群员"
    MemberPermission.ADMINISTRATOR -> "管理员"
    MemberPermission.OWNER -> "群主"
}

suspend fun Contact.uploadAsImage(url: String) =
    SweetBoy.getStream(url).use { it.uploadAsImage(this@uploadAsImage) }

suspend fun Contact.uploadAsImage(file: File) =
    file.uploadAsImage(this@uploadAsImage)

suspend fun Contact.uploadAsAudio(url: String) =
    SweetBoy.getStream(url).toExternalResource().use { (this@uploadAsAudio as AudioSupported).uploadAudio(it) }

suspend fun Contact.uploadAsAudio(file: File) =
    file.toExternalResource().use { (this@uploadAsAudio as AudioSupported).uploadAudio(it) }

suspend fun <T : Contact, R> T.runCatching(block: suspend T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        buildMessageChain {
            +PlainText("Warning! $e\n")
            if (e.cause != null) +PlainText("Caused by: ${e.cause}")
            +Image("{D3A4F304-847D-BB7B-1534-8ABFDC7575B4}.png")
        }.sendTo(this)
        Result.failure(e)
    }
}

/**
 * @author LaoLittle鸽鸽♡
 */
suspend fun MessageEvent.getOrWaitImage(): Image? =
    (message.takeIf { m -> m.contains(Image) } ?: runCatching {
        subject.sendMessage("请在30秒内发送图片...")
        nextMessage(30_000) { event -> event.message.contains(Image) }
    }.getOrElse { e ->
        when (e) {
            is TimeoutCancellationException -> {
                messageChainOf(PlainText("超时未发送!"), message.quote()).sendTo(subject)
                return null
            }
            else -> throw e
        }
    }).firstIsInstanceOrNull<Image>()

suspend fun MessageEvent.getOrWait(): MessageChain? =
    runCatching {
        this@getOrWait.nextMessage(30_000)
    }.getOrElse {
        when (it) {
            is TimeoutCancellationException -> {
                messageChainOf(PlainText("超时未发送!"), message.quote()).sendTo(subject)
                return null
            }
            else -> throw it
        }
    }

val String.consolas: String
    get() {
        val hash: HashMap<String, String> = hashMapOf(
            Pair("0", "𝟶"),
            Pair("1", "𝟷"),
            Pair("2", "𝟸"),
            Pair("3", "𝟹"),
            Pair("4", "𝟺"),
            Pair("5", "𝟻"),
            Pair("6", "𝟼"),
            Pair("7", "𝟽"),
            Pair("8", "𝟾"),
            Pair("9", "𝟿"),
            Pair("a", "𝚊"),
            Pair("b", "𝚋"),
            Pair("c", "𝚌"),
            Pair("d", "𝚍"),
            Pair("e", "𝚎"),
            Pair("f", "𝚏"),
            Pair("g", "𝚐"),
            Pair("h", "𝚑"),
            Pair("i", "𝚒"),
            Pair("j", "𝚓"),
            Pair("k", "𝚔"),
            Pair("l", "𝚕"),
            Pair("m", "𝚖"),
            Pair("n", "𝚗"),
            Pair("o", "𝚘"),
            Pair("p", "𝚙"),
            Pair("q", "𝚚"),
            Pair("r", "𝚛"),
            Pair("s", "𝚜"),
            Pair("t", "𝚝"),
            Pair("u", "𝚞"),
            Pair("v", "𝚟"),
            Pair("w", "𝚠"),
            Pair("x", "𝚡"),
            Pair("y", "𝚢"),
            Pair("z", "𝚣"),
            Pair("A", "𝙰"),
            Pair("B", "𝙱"),
            Pair("C", "𝙲"),
            Pair("D", "𝙳"),
            Pair("E", "𝙴"),
            Pair("F", "𝙵"),
            Pair("G", "𝙶"),
            Pair("H", "𝙷"),
            Pair("I", "𝙸"),
            Pair("J", "𝙹"),
            Pair("K", "𝙺"),
            Pair("L", "𝙻"),
            Pair("M", "𝙼"),
            Pair("N", "𝙽"),
            Pair("O", "𝙾"),
            Pair("P", "𝙿"),
            Pair("Q", "𝚀"),
            Pair("R", "𝚁"),
            Pair("S", "𝚂"),
            Pair("T", "𝚃"),
            Pair("U", "𝚄"),
            Pair("V", "𝚅"),
            Pair("W", "𝚆"),
            Pair("X", "𝚇"),
            Pair("Y", "𝚈"),
            Pair("Z", "𝚉")
        )
        var str = ""
        this.forEach {
            str += if (it.toString() in hash.keys) hash[it.toString()] else it.toString()
        }
        return str
    }

/**
 * @author LaoLittle鸽鸽♡
 */
object YinglishUtil {
    val String.yinglish get() = this.chs2yin(50)
    fun String.chs2yin(yingLevel: Int): String {
        val b = JiebaSegmenter().process(this, JiebaSegmenter.SegMode.SEARCH)
        var yinglish = ""
        b.forEach { keyWord ->
            val part = WordDictionary.getInstance().parts[keyWord.word]
            val chars = keyWord.word.toCharArray()
            yinglish += getYinglishNode(chars, part, yingLevel)
        }
        return yinglish
    }

    private fun getYinglishNode(chars: CharArray, part: String?, yingLevel: Int): String {
        val randomOneTen = { (1..100).random() }
        var pon = ""
        if (randomOneTen() > yingLevel)
            return String(chars)
        when (chars[0].toString()) {
            in arrayOf("!", "！", "？", "?", "—") -> return "❤"
            in arrayOf(",", "，", "。") -> return "…"
        }
        if (chars.size > 1 && randomOneTen() > 50)
            return "${chars[0]}…${String(chars)}"
        else if (part == "n" && randomOneTen() > 50) {
            repeat(chars.count()) { pon += "〇" }
            return pon
        }
        return "…${String(chars)}"
    }
}


/**
 * @author LaoLittle鸽鸽♡
 */
@Suppress("SameParameterValue")
object PatPatTool {
    @OptIn(ExperimentalCommandDescriptors::class, ConsoleExperimentalApi::class)
    fun getPat(user: User, delay: Int) {
        val qqId = user.id

        if (!tempFolder.exists()) tempFolder.mkdir()
        if (tempFolder.resolve("${qqId}_pat.gif").exists()) return
        val avatar = URL(user.avatarUrl)
        mkImg(avatar, tempFolder.resolve("${qqId}_pat.gif"), delay)
    }

    suspend fun getImagePat(image: Image, delay: Int) {
        val imageFromServer = URL(image.queryUrl())
        mkImg(imageFromServer, tempFolder.resolve("${image.imageId}_pat.gif"), delay)
    }

    private fun mkImg(image: URL, savePath: File, delay: Int) {
        val avatarImage = ImageIO.read(image)
        val targetSize = avatarImage.width
        val roundImage = BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB)
        roundImage.createGraphics().apply {
            composite = AlphaComposite.Src
            setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            color = Color.WHITE
            fill(
                RoundRectangle2D.Float(
                    0F, 0F, targetSize.toFloat(), targetSize.toFloat(),
                    targetSize.toFloat(), targetSize.toFloat()
                )
            )
            composite = AlphaComposite.SrcAtop
            drawImage(avatarImage, 0, 0, targetSize, targetSize, null)
            dispose()
        }
        roundImage.getScaledInstance(112, 112, BufferedImage.SCALE_SMOOTH).apply {
            val p1 = processImage(this, 0, 100, 100, 12, 16, 0)
            val p2 = processImage(this, 1, 105, 88, 12, 28, 0)
            val p3 = processImage(this, 2, 110, 76, 12, 40, 6)
            val p4 = processImage(this, 3, 107, 84, 12, 32, 0)
            val p5 = processImage(this, 4, 100, 100, 12, 16, 0)
            val images: Array<BufferedImage> = arrayOf(p1, p2, p3, p4, p5)
            org.seiki.GifEncoder.convert(images, "$savePath", delay)
        }
    }

    //w: 宽 h: 高 x,y: 头像位置 hy:手的y轴偏移
    @OptIn(ExperimentalCommandDescriptors::class, ConsoleExperimentalApi::class)
    private fun processImage(
        image: AwtImage, i: Int, w: Int, h: Int, x: Int, y: Int, hy: Int
    ): BufferedImage {
        val handImage = ImageIO.read(SeikiMain.javaClass.getResourceAsStream("/data/PatPat/img${i}.png"))
        val processingImage = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val processedImage = BufferedImage(112, 112, BufferedImage.TYPE_INT_RGB)
        processingImage.createGraphics().apply {
            drawImage(image, 0, 0, w, h, null)
            dispose()
        }
        processedImage.createGraphics().apply {
            color = Color.WHITE
            fillRect(0, 0, 112, 112)
            drawImage(processingImage, x, y, null)
            drawImage(handImage, 0, hy, null)
            dispose()
        }
        return processedImage
    }
}