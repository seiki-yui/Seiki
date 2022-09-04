@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.seiki.plugin

import com.huaban.analysis.jieba.JiebaSegmenter
import com.huaban.analysis.jieba.WordDictionary
import kotlinx.coroutines.TimeoutCancellationException
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.nextMessage
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.jetbrains.skia.*
import org.laolittle.plugin.getBytes
import org.laolittle.plugin.gif.GifImage
import org.laolittle.plugin.gif.GifSetting
import org.laolittle.plugin.gif.buildGifImage
import org.seiki.SweetBoy
import org.seiki.plugin.SkikoUtil.bar
import org.seiki.plugin.SkikoUtil.makeFromResource
import org.seiki.plugin.UnvcodeUtil.MathUtil.minIndex
import org.seiki.plugin.UnvcodeUtil.MathUtil.variance
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.*
import java.text.Normalizer
import org.jetbrains.skia.Image as SkiaImage
import org.jetbrains.skia.Canvas as SkiaCanvas
import org.jetbrains.skia.Color as SkiaColor
import org.jetbrains.skia.Paint as SkiaPaint
import org.jetbrains.skia.Point as SkiaPoint
import java.awt.Color as AwtColor
import java.awt.Font as AwtFont
import java.awt.Image as AwtImage
import javax.imageio.ImageIO
import kotlin.math.*
import kotlin.random.Random

val ownerList = arrayListOf(2630557998L, 1812691029L)

val biliVideoRegex =
    """[\s\S]*?(?:(?:https?://)?(?:www\.)?bilibili\.com/video/)?([aA][vV]\d+|[bB][vV][a-zA-Z\d]+)[\s\S]*?""".toRegex()
val bili23tvRegex = """[\s\S]*?((?:https?://)?(?:www\.)?b23\.tv/[a-zA-Z\d]+)[\s\S]*?""".toRegex()
val biliUserRegex = """[\s\S]*?(?:https?://)?(?:space\.bilibili\.com|bilibili\.com/space)/(\d+)[\s\S]*?""".toRegex()
val biliUserIDRegex = """[\s\S]*?[uU][iI][dD]:?(\d+)[\s\S]*?""".toRegex()

val MemberPermission.levelName: String
    get() = when (this) {
        MemberPermission.MEMBER -> "群员"
        MemberPermission.ADMINISTRATOR -> "管理员"
        MemberPermission.OWNER -> "群主"
    }

val User.name: String get() = "${this.nameCardOrNick}(${this.id})"

suspend fun Contact.uploadAsImage(url: String) =
    SweetBoy.getStream(url).use { it.uploadAsImage(this@uploadAsImage) }

suspend fun Contact.uploadAsImage(file: File) =
    file.uploadAsImage(this@uploadAsImage)

suspend fun Contact.uploadAsAudio(url: String) =
    SweetBoy.getStream(url).toExternalResource().use { (this@uploadAsAudio as AudioSupported).uploadAudio(it) }

suspend fun Contact.uploadAsAudio(file: File) =
    file.toExternalResource().use { (this@uploadAsAudio as AudioSupported).uploadAudio(it) }

fun String.convert(max: Int = 200): ArrayList<String> {
    var num = 0
    var len = 0
    var str = ""
    val list: ArrayList<String> = arrayListOf()
    this.split("\n").forEach {
        num += it.length
        len += it.length
        if (num < max) str += it + "\n" else {
            list.add(str)
            str = ""
            num = 0
        }
    }
    if (len < max) list.add(str)
    return list
}

fun Throwable.buildMessage() = buildMessageChain {
    +PlainText("Warning! ${this@buildMessage}\n")
    if (this@buildMessage.cause != null) +PlainText("Caused by: ${this@buildMessage.cause}")
    +Image("{D3A4F304-847D-BB7B-1534-8ABFDC7575B4}.png")
}
suspend fun <T : Contact, R> T.runCatching(block: suspend T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        e.buildMessage().sendTo(this)
        Result.failure<R>(e).also { throw e }
    }
}


/**
 * @author LaoLittle鸽鸽♡
 */
internal suspend fun MessageEvent.getOrWaitImage(msg: String = "请在30秒内发送图片..."): Image? =
    (message.takeIf { m -> m.contains(Image) } ?: runCatching {
        subject.sendMessage(msg)
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

@Deprecated("不适用", level = DeprecationLevel.WARNING)
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
    val String.yinglish get() = this.chs2yin(100)
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
            in arrayOf(",", "，", "。") -> return "..."
        }
        if (chars.size > 1 && randomOneTen() > 50)
            return "${chars[0]}❤...${String(chars)}"
        else if (part == "n" && randomOneTen() > 50) {
            repeat(chars.count()) { pon += "〇" }
            return pon
        }
        return "...${String(chars)}"
    }
}

/**
 * @author LaoLittle鸽鸽♡
 */
object SkikoUtil {
    fun SkiaCanvas.bar(block: SkiaCanvas.() -> Unit) {
        save()
        block()
        restore()
    }

    fun Rect.copy(
        left: Float = this.left,
        top: Float = this.top,
        right: Float = this.right,
        bottom: Float = this.bottom
    ) = Rect(left, top, right, bottom)

    internal fun SkiaImage.Companion.makeFromResource(name: String) = makeFromEncoded(
        SeikiMain::class.java.getResourceAsStream(name)?.readBytes() ?: throw IllegalStateException("无法找到资源文件: $name")
    )
}

/**
 * @author LaoLittle鸽鸽♡
 */
object PatPatUtil {
    private const val width = 320
    private const val height = 320

    suspend fun patpat(image: SkiaImage, delay: Double = .05): GifImage {
        return buildGifImage(GifSetting(width, height, 100, true, GifSetting.Repeat.Infinite)) {
            addFrame(pat(Rect(40f, 40f, 300f, 300f), SkiaPoint(0f, 0f), image, 0).getBytes(), delay)
            addFrame(pat(Rect(40f, 70f, 300f, 300f), SkiaPoint(0f, 0f), image, 1).getBytes(), delay)
            addFrame(pat(Rect(33f, 105f, 300f, 300f), SkiaPoint(0f, 0f), image, 2).getBytes(), delay)
            addFrame(pat(Rect(37f, 90f, 300f, 300f), SkiaPoint(0f, 0f), image, 3).getBytes(), delay)
            addFrame(pat(Rect(40f, 65f, 300f, 300f), SkiaPoint(0f, 0f), image, 4).getBytes(), delay)
        }
    }

    private val whitePaint = SkiaPaint().apply { color = SkiaColor.WHITE }
    private val srcInPaint = SkiaPaint().apply { blendMode = BlendMode.SRC_IN }
    private val hands = Array(5) { SkiaImage.makeFromResource("/data/PatPat/img$it.png") }

    private const val imgW = width.toFloat()
    private const val imgH = height.toFloat()
    fun pat(imgDst: Rect, handPoint: SkiaPoint, image: SkiaImage, no: Int): SkiaImage {
        val hand = hands[no]
        return Surface.makeRasterN32Premul(width, height).apply {
            canvas.apply {
                bar {
                    val radius = (width shr 1).toFloat()
                    translate(imgDst.left, imgDst.top)
                    scale(imgDst.width / width, imgDst.height / height)
                    drawCircle(imgW * .5f, imgH * .5f, radius, whitePaint)
                    drawImageRect(
                        image,
                        Rect.makeWH(image.width.toFloat(), image.height.toFloat()),
                        Rect.makeWH(imgW, imgH),
                        FilterMipmap(FilterMode.LINEAR, MipmapMode.NEAREST),
                        srcInPaint,
                        true
                    )
                }
                drawImageRect(
                    hand,
                    Rect.makeWH(hand.width.toFloat(), hand.height.toFloat()),
                    Rect(handPoint.x, handPoint.y, handPoint.x + width, handPoint.y + height),
                    SamplingMode.CATMULL_ROM,
                    null,
                    true
                )
            }
        }.makeImageSnapshot()
    }
}

object UnvcodeUtil {
    object MathUtil {
        fun List<Int>.sum(): Int {
            var sum = 0
            this.forEach { sum += it }
            return sum
        }

        fun List<Int>.average(): Double = this.sum().toDouble() / this.size

        fun List<Int>.variance(): Double {
            val average = this.average()
            var variance = 0.0
            this.forEach {
                variance += ((it - average).pow(2.0))
            }
            return variance / this.size
        }

        infix fun List<Int>.minus(other: List<Int>): List<Int> {
            if (this.size != other.size) throw Throwable("?!")
            return mutableListOf<Int>().apply {
                for (i in 0..this@minus.lastIndex) {
                    add(this@minus[i] - other[i])
                }
            }
        }

        fun List<Double>.minIndex(): Int {
            var minIndex = 0
            for (i in 1..this.lastIndex) {
                if (this[i] < this[minIndex])minIndex=i
            }
            return minIndex
        }
    }

    private val d = mutableMapOf<Char, MutableList<Char>>()

    init {
        for (i in 0 until 65536) {
            val word = i.toChar()
            val key = Normalizer.normalize(word.toString(), Normalizer.Form.NFKC).toCharArray()[0]
            if (word != key) {
                d.putIfAbsent(key, mutableListOf())
                d[key]?.add(word)
            }
        }
    }

    private fun draw(key: Char): List<Int> {
        var img = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
        val fnt = AwtFont(null, AwtFont.PLAIN, 100)
        var g2d = img.createGraphics()
        g2d.font = fnt
        val fm = g2d.fontMetrics
        val width = fm.stringWidth(key.toString())
        val height = fm.height
        g2d.dispose()
        img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        g2d = img.createGraphics()
        g2d.color = AwtColor.BLACK
        g2d.fillRect(0, 0, img.width, img.height)
        g2d.color = AwtColor.WHITE
        g2d.font = fnt
        g2d.drawString(key.toString(), 0, fm.ascent)
        g2d.dispose()
        //ImageIO.write(img, "png", File("/Users/lz233/Desktop/1.png"))
        return mutableListOf<Int>().apply {
            (img.raster.dataBuffer as DataBufferInt).data.forEach {
                val color = AwtColor(it)
                add(color.red / 255)
                add(color.green / 255)
                add(color.blue / 255)
            }
        }
    }

    private fun compare(key1: Char, key2: Char) = (draw(key1) - draw(key2).toSet()).variance()

    private fun masquerade(key: Char, skipAscii: Boolean, mse: Double = 0.1): Pair<Double, Char> {
        if ((key.code < 128) and skipAscii) return (-1.0 to key)
        val candidateGroup = d[key] ?: return (-1.0 to key)
        val differenceGroup = mutableListOf<Double>().apply {
            candidateGroup.forEach { add(compare(key, it)) }
        }
        val difference = differenceGroup.minOrNull()!!
        val new = candidateGroup[differenceGroup.minIndex()]
        return if (difference > mse) (-1.0 to key) else (difference to new)
    }

    fun convert(s: String, skipAscii: Boolean = true, mse: Double = 0.1): Pair<String, List<Double>> {
        val differenceList = mutableListOf<Double>()
        val str = StringBuilder().apply {
            s.toCharArray().forEach {
                val result = masquerade(it, skipAscii, mse)
                differenceList.add(result.first)
                append(result.second)
            }
        }.toString()
        return (str to differenceList)
    }

    fun String.unvcode(skipAscii: Boolean = true, mse: Double = 0.1) = convert(this, skipAscii, mse)

    val String.unvcode get() = this.unvcode().first
}

@Suppress("duplicates", "NAME_SHADOWING")
object MirageUtils {
    private val logger get() = SeikiMain.logger

    // 亮、暗色阶映射表
    private var mLightColorTable: IntArray? = null
    private var mDarkColorTable: IntArray? = null

    // 生成幻影坦克
    fun buildMirageTank(pathOut:String,pathIn:String,savePath:String){
        var picA = setImageOne(pathOut)!!
        var picB = setImageTwo(pathIn)!!
        val targetList: List<BufferedImage?> = picResize(picA, picB)
        if (targetList.isNotEmpty()) {
            if (targetList[0] != null) {
                picA = targetList[0]!!
            }
            if (targetList[1] != null) {
                picB = targetList[1]!!
            }
        }
        setGray(picA)
        changeColorLevel(picA, true)
        opposition(picA)
        setGray(picB)
        changeColorLevel(picB, false)
        picA = linearDodge(picA, picB)
        val temp: BufferedImage = redChannels(picA)
        picA = divide(picA, picB)
        val result: BufferedImage = masking(picA, temp)
        saveFile(savePath, result)
    }

    private fun getImage(path: String?): BufferedImage? {
        val file = File(path!!)
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
        } catch (e1: FileNotFoundException) {
            e1.printStackTrace()
        }
        try {
            if (fis != null) {
                return ImageIO.read(fis)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun setImageOne(pathOne: String?): BufferedImage? {
        return getImage(pathOne)
    }

    private fun setImageTwo(pathTwo: String?): BufferedImage? {
        return getImage(pathTwo)
    }

    private fun saveFile(savePath: String?, result: BufferedImage?) {
        val file = File(savePath!!)
        try {
            if (!file.exists()) {
                file.createNewFile()
                ImageIO.write(result, "png", file)
            }
        } catch (e: Exception) {
            logger.error(e)
        }
    }

    // 重设图片大小
    private fun picResize(targetTop: BufferedImage, targetBottom: BufferedImage): List<BufferedImage?>{
        val topWidth = targetTop.width
        val topHeight = targetTop.height
        val bottomWidth = targetBottom.width
        val bottomHeight = targetBottom.height
        val targetList: MutableList<BufferedImage?> = ArrayList()

        if((topWidth == bottomWidth) && (topHeight == bottomHeight)){
            targetList.add(targetTop)
            targetList.add(targetBottom)
        }else{
            val scaleRatio = min(
                ((topWidth * 1f)/(bottomWidth * 1f)),
                ((topHeight * 1f)/(bottomHeight * 1f))
            )
            if(topWidth * topHeight > bottomWidth * bottomHeight){
                val scaledWidth = (targetBottom.width * scaleRatio).toInt()
                val scaledHeight = (targetBottom.height * scaleRatio).toInt()
                val scaleBottom = targetBottom.getScaledInstance(
                    scaledWidth,
                    scaledHeight,
                    AwtImage.SCALE_DEFAULT
                )
                val resultBottom = BufferedImage(topWidth, topHeight, BufferedImage.TYPE_INT_ARGB)
                val graphics = resultBottom.createGraphics()
                graphics.drawImage(
                    scaleBottom,
                    (topWidth-scaledWidth) / 2,
                    (topHeight-scaledHeight) / 2,
                    null
                )
                graphics.dispose()

                targetList.add(targetTop)
                targetList.add(resultBottom)
            }else{
                val scaledWidth = (targetTop.width / scaleRatio).toInt()
                val scaledHeight = (targetTop.height / scaleRatio).toInt()
                val scaleTop = targetTop.getScaledInstance(scaledWidth,scaledHeight,AwtImage.SCALE_DEFAULT)
                val resultBottom = BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB)
                val graphics = resultBottom.createGraphics()
                graphics.drawImage(
                    targetBottom,
                    (scaledWidth-bottomWidth) / 2,
                    (scaledHeight-bottomHeight) / 2,
                    null
                )
                graphics.dispose()
                targetList.add(image2BufferedImage(scaleTop,scaledWidth,scaledHeight))
                targetList.add(resultBottom)
            }
        }
        return targetList
    }

    private fun image2BufferedImage(image:AwtImage,width:Int,height:Int):BufferedImage{
        val resultBuffered = BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB)
        val graphics = resultBuffered.createGraphics()
        graphics.drawImage(image,0,0,null)
        graphics.dispose()
        return resultBuffered
    }
    //去色
    private fun setGray(target: BufferedImage) {
        val width = target.width
        val height = target.height
        val targetPixels = IntArray(width * height)
        getBitmapPixelColor(target, object : PixelColorHandler {
            override fun onHandle(x: Int, y: Int, a: Int, r: Int, g: Int, b: Int) {
                val gray = (r + g + b) / 3
                targetPixels[x + y * width] = getIntFromColor(a, gray, gray, gray)
            }
        })
        target.setRGB(0, 0, width, height, targetPixels, 0, width)
    }

    // 红色通道
    private fun redChannels(target: BufferedImage): BufferedImage {
        val width = target.width
        val height = target.height
        val targetPixels = IntArray(width * height)
        val result = BufferedImage(target.width, target.height,
            BufferedImage.TYPE_INT_ARGB)
        getBitmapPixelColor(target, object : PixelColorHandler{
            override fun onHandle(x: Int, y: Int, a: Int, r: Int, g: Int, b: Int) {
                targetPixels[x + y * width] = getIntFromColor(r, r, g, b)
            }
        })
        result.setRGB(0, 0, width, height, targetPixels, 0, width)

        return result
    }

    // 蒙版
    private fun masking(src: BufferedImage, target: BufferedImage): BufferedImage {
        val width = src.width
        val height = src.height
        val srcPixels = IntArray(width * height)
        val result = BufferedImage(src.width, src.height, BufferedImage.TYPE_INT_ARGB)
        getBitmapPixelColor(src, object : PixelColorHandler {
            override fun onHandle(x: Int, y: Int, a: Int, r: Int, g: Int, b: Int) {
                val dstA: Int
                val dstPixelColor: Int = target.getRGB(x, y)
                dstA = dstPixelColor and -0x1000000 shr 24
                srcPixels[x + y * width] = getIntFromColor(dstA, r, g, b)
            }
        })
        result.setRGB(0, 0, width, height, srcPixels, 0, width)
        return result
    }

    // 反相
    private fun opposition(target: BufferedImage) {
        val width = target.width
        val height = target.height
        val targetPixels = IntArray(width * height)
        getBitmapPixelColor(target, object : PixelColorHandler {
            override fun onHandle(x: Int, y: Int, a: Int, r: Int, g: Int, b: Int) {
                val max = 255
                targetPixels[x + y * width] = getIntFromColor(a, max - r, max - g, max - b)
            }
        })
        target.setRGB(0, 0, width, height, targetPixels, 0, width)
    }

    //划分
    private fun divide(src: BufferedImage, target: BufferedImage): BufferedImage {
        val width = src.width
        val height = src.height
        val srcPixels = IntArray(width * height)
        val result = BufferedImage(src.width, src.height, BufferedImage.TYPE_INT_ARGB)
        getBitmapPixelColor(src, object : PixelColorHandler {
            override fun onHandle(x: Int, y: Int, a: Int, r: Int, g: Int, b: Int) {
                val dstR: Int
                val dstG: Int
                val dstB: Int
                val dstPixelColor: Int = target.getRGB(x, y)
                dstR = dstPixelColor and 0xFF0000 shr 16
                dstG = dstPixelColor and 0xFF00 shr 8
                dstB = dstPixelColor and 0xFF
                val resultA = 255
                val resultR: Int = (255 / (r.toFloat() / dstR.toFloat())).toInt()
                val resultG: Int = (255 / (g.toFloat() / dstG.toFloat())).toInt()
                val resultB: Int = (255 / (b.toFloat() / dstB.toFloat())).toInt()
                srcPixels[x + y * width] = getIntFromColor(resultA, resultR, resultG, resultB)
            }
        })
        result.setRGB(0, 0, width, height, srcPixels, 0, width)
        return result
    }

    //线性减淡
    private fun linearDodge(src: BufferedImage, target: BufferedImage): BufferedImage {
        val width = src.width
        val height = src.height
        val srcPixels = IntArray(width * height)
        val result = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        getBitmapPixelColor(src, object : PixelColorHandler {
            override fun onHandle(x: Int, y: Int, a: Int, r: Int, g: Int, b: Int) {
                val dstR: Int
                val dstG: Int
                val dstB: Int
                var resultR: Int
                var resultG: Int
                var resultB: Int
                val dstPixelColor: Int = target.getRGB(x, y)
                dstR = dstPixelColor and 0xFF0000 shr 16
                dstG = dstPixelColor and 0xFF00 shr 8
                dstB = dstPixelColor and 0xFF
                val resultA = 255
                resultR = r + dstR
                resultG = g + dstG
                resultB = b + dstB
                // 防止色值溢出
                if (resultR > 255) resultR = 255
                if (resultG > 255) resultG = 255
                if (resultB > 255) resultB = 255
                srcPixels[x + y * width] = getIntFromColor(resultA, resultR, resultG, resultB)
            }
        })
        result.setRGB(0, 0, width, height, srcPixels, 0, width)
        return result
    }

    // 调整色阶, true->up, false->down
    private fun changeColorLevel(target: BufferedImage, isToLight: Boolean) {
        val width = target.width
        val height = target.height
        val targetPixels = IntArray(width * height)
        val table = if (isToLight) lightColorTable else darkColorTable
        getBitmapPixelColor(target, object : PixelColorHandler {
            override fun onHandle(x: Int, y: Int, a: Int, r: Int, g: Int, b: Int) {
                targetPixels[x + y * width] = getIntFromColor(a, table!![r], table[g], table[b])
            }
        })
        target.setRGB(0, 0, width, height, targetPixels, 0, width)
    }

    private val lightColorTable: IntArray?
        get() {
            if (mLightColorTable == null) initLightColorTable()
            return mLightColorTable
        }
    private val darkColorTable: IntArray?
        get() {
            if (mDarkColorTable == null) initDarkColorTable()
            return mDarkColorTable
        }

    private fun getBitmapPixelColor(target: BufferedImage, handler: PixelColorHandler) {
        var a: Int
        var r: Int
        var g: Int
        var b: Int
        var pixelColor: Int
        for (y in 0 until target.height) {
            for (x in 0 until target.width) {
                pixelColor = target.getRGB(x, y)
                a = pixelColor and -0x1000000 shr 24
                r = pixelColor and 0xFF0000 shr 16
                g = pixelColor and 0xFF00 shr 8
                b = pixelColor and 0xFF
                handler.onHandle(x, y, a, r, g, b)
            }
        }
    }

    // Color.argb
    private fun getIntFromColor(alpha: Int, red: Int, green: Int, blue: Int): Int {
        var alpha = alpha
        var red = red
        var green = green
        var blue = blue
        alpha = alpha shl 24 and -0x1000000
        red = red shl 16 and 0x00FF0000 // Shift red 16-bits and mask out other stuff
        green = green shl 8 and 0x0000FF00 // Shift Green 8-bits and mask out other stuff
        blue = blue and 0x000000FF // Mask out anything not blue.
        return 0x00000000 or alpha or red or green or blue
    }

    private fun initLightColorTable() {
        // 输出色阶 120 ～ 255 的映射表
        // 由 getColorLevelTable(120, 255); 得来
        mLightColorTable = intArrayOf(120, 120, 121, 121, 122, 122, 123, 123, 124, 124, 125, 125, 126, 126, 127, 127,
            128, 128, 129, 129, 130, 130, 131, 132, 132, 133, 133, 134, 134, 135, 135, 136, 136, 137, 137, 138, 138,
            139, 139, 140, 140, 141, 142, 142, 143, 143, 144, 144, 145, 145, 146, 146, 147, 147, 148, 148, 149, 149,
            150, 150, 151, 152, 152, 153, 153, 154, 154, 155, 155, 156, 156, 157, 157, 158, 158, 159, 159, 160, 161,
            161, 162, 162, 163, 163, 164, 164, 165, 165, 166, 166, 167, 167, 168, 168, 169, 170, 170, 171, 171, 172,
            172, 173, 173, 174, 174, 175, 175, 176, 176, 177, 177, 178, 179, 179, 180, 180, 181, 181, 182, 182, 183,
            183, 184, 184, 185, 185, 186, 186, 187, 188, 188, 189, 189, 190, 190, 191, 191, 192, 192, 193, 193, 194,
            194, 195, 195, 196, 197, 197, 198, 198, 199, 199, 200, 200, 201, 201, 202, 202, 203, 203, 204, 205, 205,
            206, 206, 207, 207, 208, 208, 209, 209, 210, 210, 211, 211, 212, 212, 213, 214, 214, 215, 215, 216, 216,
            217, 217, 218, 218, 219, 219, 220, 220, 221, 222, 222, 223, 223, 224, 224, 225, 225, 226, 226, 227, 227,
            228, 228, 229, 229, 230, 231, 231, 232, 232, 233, 233, 234, 234, 235, 235, 236, 236, 237, 237, 238, 239,
            239, 240, 240, 241, 241, 242, 242, 243, 243, 244, 244, 245, 245, 246, 247, 247, 248, 248, 249, 249, 250,
            250, 251, 251, 252, 252, 253, 253, 254, 255)
    }

    private fun initDarkColorTable() {
        // 输出色阶 0 ～ 135 的映射表
        // 由 getColorLevelTable(0, 135); 得来
        mDarkColorTable = intArrayOf(0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 12, 12,
            13, 13, 14, 14, 15, 15, 16, 16, 17, 17, 18, 18, 19, 19, 20, 20, 21, 22, 22, 23, 23, 24, 24, 25, 25, 26,
            26, 27, 27, 28, 28, 29, 29, 30, 30, 31, 32, 32, 33, 33, 34, 34, 35, 35, 36, 36, 37, 37, 38, 38, 39, 39,
            40, 41, 41, 42, 42, 43, 43, 44, 44, 45, 45, 46, 46, 47, 47, 48, 48, 49, 50, 50, 51, 51, 52, 52, 53, 53,
            54, 54, 55, 55, 56, 56, 57, 57, 58, 59, 59, 60, 60, 61, 61, 62, 62, 63, 63, 64, 64, 65, 65, 66, 66, 67,
            68, 68, 69, 69, 70, 70, 71, 71, 72, 72, 73, 73, 74, 74, 75, 75, 76, 77, 77, 78, 78, 79, 79, 80, 80, 81,
            81, 82, 82, 83, 83, 84, 85, 85, 86, 86, 87, 87, 88, 88, 89, 89, 90, 90, 91, 91, 92, 92, 93, 94, 94, 95,
            95, 96, 96, 97, 97, 98, 98, 99, 99, 100, 100, 101, 102, 102, 103, 103, 104, 104, 105, 105, 106, 106,
            107, 107, 108, 108, 109, 109, 110, 111, 111, 112, 112, 113, 113, 114, 114, 115, 115, 116, 116, 117, 117,
            118, 119, 119, 120, 120, 121, 121, 122, 122, 123, 123, 124, 124, 125, 125, 126, 127, 127, 128, 128, 129,
            129, 130, 130, 131, 131, 132, 132, 133, 133, 134, 135)
    }

    private fun getColorLevelTable(outputMin: Int, outputMax: Int): IntArray {
        var outputMin = outputMin
        var outputMax = outputMax
        val data = IntArray(256)
        val inputMin = 0
        val inputMiddle = 128
        val inputMax = 255
        if (outputMin < 0) outputMin = 0
        if (outputMin > 255) outputMin = 255
        if (outputMax < 0) outputMax = 0
        if (outputMax > 255) outputMax = 255
        for (index in 0..255) {
            var temp = (index - inputMin).toDouble()
            temp = if (temp < 0) {
                outputMin.toDouble()
            } else if (temp + inputMin > inputMax) {
                outputMax.toDouble()
            } else {
                val gamma = ln(0.5) / ln((inputMiddle - inputMin).toDouble() / (inputMax - inputMin))
                outputMin + (outputMax - outputMin) * (temp / (inputMax - inputMin)).pow(gamma)
            }
            if (temp > 255) temp = 255.0 else if (temp < 0) temp = 0.0
            data[index] = temp.toInt()
        }
        return data
    }

    interface PixelColorHandler {
        fun onHandle(x: Int, y: Int, a: Int, r: Int, g: Int, b: Int)
    }

    // InputStream -> File
    @Throws(IOException::class)
    fun copyInputStreamToFile(inputStream: InputStream, file: File) {
        FileOutputStream(file).use { outputStream ->
            var read: Int
            val bytes = ByteArray(1024)
            while (inputStream.read(bytes).also { read = it } != -1) {
                outputStream.write(bytes, 0, read)
            }
        }
    }

    // 如不存在则创建目录
    fun touchDir(dirPath: String): Boolean {
        var destDirName = dirPath
        val dir = File(destDirName)
        if (dir.exists()) {
            return false
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName += File.separator
        }
        //创建目录
        return if (dir.mkdirs()) {
            true
        } else {
            logger.error("创建目录" + destDirName + "失败！")
            false
        }
    }


    internal fun convertToJPG(imagePath:String){
        val bufferedImage: BufferedImage
        try {
            //read image file
            bufferedImage = ImageIO.read(File(imagePath))

            // create a blank, RGB, same width and height, and a white background
            val newBufferedImage = BufferedImage(bufferedImage.width,
                bufferedImage.height, BufferedImage.TYPE_INT_RGB)

            //TYPE_INT_RGB:创建一个RBG图像，24位深度，成功将32位图转化成24位
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, AwtColor.WHITE, null)

            // write to jpeg file
            ImageIO.write(newBufferedImage, "jpg", File(imagePath))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

object MathUtil {
    fun bilinearInterpolate(x: Float, y: Float, nw: Int, ne: Int, sw: Int, se: Int): Int {
        var m0: Float
        var m1: Float
        val a0 = nw shr 24 and 0xff
        val r0 = nw shr 16 and 0xff
        val g0 = nw shr 8 and 0xff
        val b0 = nw and 0xff
        val a1 = ne shr 24 and 0xff
        val r1 = ne shr 16 and 0xff
        val g1 = ne shr 8 and 0xff
        val b1 = ne and 0xff
        val a2 = sw shr 24 and 0xff
        val r2 = sw shr 16 and 0xff
        val g2 = sw shr 8 and 0xff
        val b2 = sw and 0xff
        val a3 = se shr 24 and 0xff
        val r3 = se shr 16 and 0xff
        val g3 = se shr 8 and 0xff
        val b3 = se and 0xff
        val cx = 1.0f - x
        val cy = 1.0f - y
        m0 = cx * a0 + x * a1
        m1 = cx * a2 + x * a3
        val a = (cy * m0 + y * m1).toInt()
        m0 = cx * r0 + x * r1
        m1 = cx * r2 + x * r3
        val r = (cy * m0 + y * m1).toInt()
        m0 = cx * g0 + x * g1
        m1 = cx * g2 + x * g3
        val g = (cy * m0 + y * m1).toInt()
        m0 = cx * b0 + x * b1
        m1 = cx * b2 + x * b3
        val b = (cy * m0 + y * m1).toInt()
        return a shl 24 or (r shl 16) or (g shl 8) or b
    }
    /**
     * Perlin Noise functions
     */
    object Noise {
        fun evaluate(x: Float): Float {
            return noise1(x)
        }

        fun evaluate(x: Float, y: Float): Float {
            return noise2(x, y)
        }

        fun evaluate(x: Float, y: Float, z: Float): Float {
            return noise3(x, y, z)
        }

        private val randomGenerator = Random

        /**
         * Compute turbulence using Perlin noise.
         * @param x the x value
         * @param y the y value
         * @param octaves number of octaves of turbulence
         * @return turbulence value at (x,y)
         */
        fun turbulence2(x: Float, y: Float, octaves: Float): Float {
            var t = 0.0f
            var f = 1.0f
            while (f <= octaves) {
                t += abs(noise2(f * x, f * y)) / f
                f *= 2f
            }
            return t
        }

        /**
         * Compute turbulence using Perlin noise.
         * @param x the x value
         * @param y the y value
         * @param octaves number of octaves of turbulence
         * @return turbulence value at (x,y)
         */
        fun turbulence3(x: Float, y: Float, z: Float, octaves: Float): Float {
            var t = 0.0f
            var f = 1.0f
            while (f <= octaves) {
                t += abs(noise3(f * x, f * y, f * z)) / f
                f *= 2f
            }
            return t
        }

        private const val B = 0x100
        private const val BM = 0xff
        private const val N = 0x1000
        private val p = IntArray(B + B + 2)
        private val g3 = Array(B + B + 2) {
            FloatArray(
                3
            )
        }
        private val g2 = Array(B + B + 2) {
            FloatArray(
                2
            )
        }
        private val g1 = FloatArray(B + B + 2)
        private var start = true
        private fun sCurve(t: Float): Float {
            return t * t * (3.0f - 2.0f * t)
        }

        /**
         * Compute 1-dimensional Perlin noise.
         * @param x the x value
         * @return noise value at x in the range -1..1
         */
        fun noise1(x: Float): Float {
            val bx0: Int
            val rx0: Float
            if (start) {
                start = false
                init()
            }
            val t: Float = x + N
            bx0 = t.toInt() and BM
            val bx1: Int = bx0 + 1 and BM
            rx0 = t - t.toInt()
            val rx1: Float = rx0 - 1.0f
            val sx: Float = sCurve(rx0)
            val u: Float = rx0 * g1[p[bx0]]
            val v: Float = rx1 * g1[p[bx1]]
            return 2.3f * lerp(sx, u, v)
        }

        /**
         * Compute 2-dimensional Perlin noise.
         * @param x the x coordinate
         * @param y the y coordinate
         * @return noise value at (x,y)
         */
        fun noise2(x: Float, y: Float): Float {
            val bx0: Int
            val by0: Int
            val b00: Int
            val b10: Int
            val b01: Int
            val b11: Int
            val rx0: Float
            val ry0: Float
            val a: Float
            val b: Float
            var u: Float
            var v: Float
            val j: Int
            if (start) {
                start = false
                init()
            }
            var t: Float = x + N
            bx0 = t.toInt() and BM
            val bx1: Int = bx0 + 1 and BM
            rx0 = t - t.toInt()
            val rx1: Float = rx0 - 1.0f
            t = y + N
            by0 = t.toInt() and BM
            val by1: Int = by0 + 1 and BM
            ry0 = t - t.toInt()
            val ry1: Float = ry0 - 1.0f
            val i: Int = p[bx0]
            j = p[bx1]
            b00 = p[i + by0]
            b10 = p[j + by0]
            b01 = p[i + by1]
            b11 = p[j + by1]
            val sx: Float = sCurve(rx0)
            val sy: Float = sCurve(ry0)
            var q: FloatArray = g2[b00]
            u = rx0 * q[0] + ry0 * q[1]
            q = g2[b10]
            v = rx1 * q[0] + ry0 * q[1]
            a = lerp(sx, u, v)
            q = g2[b01]
            u = rx0 * q[0] + ry1 * q[1]
            q = g2[b11]
            v = rx1 * q[0] + ry1 * q[1]
            b = lerp(sx, u, v)
            return 1.5f * lerp(sy, a, b)
        }

        /**
         * Compute 3-dimensional Perlin noise.
         * @param x the x coordinate
         * @param y the y coordinate
         * @param y the y coordinate
         * @return noise value at (x,y,z)
         */
        fun noise3(x: Float, y: Float, z: Float): Float {
            val bx0: Int
            val by0: Int
            val bz0: Int
            val b00: Int
            val b10: Int
            val b01: Int
            val b11: Int
            val rx0: Float
            val ry0: Float
            val rz0: Float
            var a: Float
            var b: Float
            val c: Float
            val d: Float
            var u: Float
            var v: Float
            val j: Int
            if (start) {
                start = false
                init()
            }
            var t: Float = x + N
            bx0 = t.toInt() and BM
            val bx1: Int = bx0 + 1 and BM
            rx0 = t - t.toInt()
            val rx1: Float = rx0 - 1.0f
            t = y + N
            by0 = t.toInt() and BM
            val by1: Int = by0 + 1 and BM
            ry0 = t - t.toInt()
            val ry1: Float = ry0 - 1.0f
            t = z + N
            bz0 = t.toInt() and BM
            val bz1: Int = bz0 + 1 and BM
            rz0 = t - t.toInt()
            val rz1: Float = rz0 - 1.0f
            val i: Int = p[bx0]
            j = p[bx1]
            b00 = p[i + by0]
            b10 = p[j + by0]
            b01 = p[i + by1]
            b11 = p[j + by1]
            t = sCurve(rx0)
            val sy: Float = sCurve(ry0)
            val sz: Float = sCurve(rz0)
            var q: FloatArray = g3[b00 + bz0]
            u = rx0 * q[0] + ry0 * q[1] + rz0 * q[2]
            q = g3[b10 + bz0]
            v = rx1 * q[0] + ry0 * q[1] + rz0 * q[2]
            a = lerp(t, u, v)
            q = g3[b01 + bz0]
            u = rx0 * q[0] + ry1 * q[1] + rz0 * q[2]
            q = g3[b11 + bz0]
            v = rx1 * q[0] + ry1 * q[1] + rz0 * q[2]
            b = lerp(t, u, v)
            c = lerp(sy, a, b)
            q = g3[b00 + bz1]
            u = rx0 * q[0] + ry0 * q[1] + rz1 * q[2]
            q = g3[b10 + bz1]
            v = rx1 * q[0] + ry0 * q[1] + rz1 * q[2]
            a = lerp(t, u, v)
            q = g3[b01 + bz1]
            u = rx0 * q[0] + ry1 * q[1] + rz1 * q[2]
            q = g3[b11 + bz1]
            v = rx1 * q[0] + ry1 * q[1] + rz1 * q[2]
            b = lerp(t, u, v)
            d = lerp(sy, a, b)
            return 1.5f * lerp(sz, c, d)
        }

        fun lerp(t: Float, a: Float, b: Float): Float {
            return a + t * (b - a)
        }

        private fun normalize2(v: FloatArray) {
            val s = sqrt((v[0] * v[0] + v[1] * v[1]).toDouble()).toFloat()
            v[0] = v[0] / s
            v[1] = v[1] / s
        }

        fun normalize3(v: FloatArray) {
            val s = sqrt((v[0] * v[0] + v[1] * v[1] + v[2] * v[2]).toDouble()).toFloat()
            v[0] = v[0] / s
            v[1] = v[1] / s
            v[2] = v[2] / s
        }

        private fun random(): Int {
            return randomGenerator.nextInt() and 0x7fffffff
        }

        private fun init() {
            var j: Int
            var k: Int
            var i = 0
            while (i < B) {
                p[i] = i
                g1[i] = (random() % (B + B) - B).toFloat() / B
                j = 0
                while (j < 2) {
                    g2[i][j] = (random() % (B + B) - B).toFloat() / B
                    j++
                }
                normalize2(g2[i])
                j = 0
                while (j < 3) {
                    g3[i][j] = (random() % (B + B) - B).toFloat() / B
                    j++
                }
                normalize3(g3[i])
                i++
            }
            i = B - 1
            while (i >= 0) {
                k = p[i]
                p[i] = p[random() % B.also {
                    j = it
                }]
                p[j] = k
                i--
            }
            i = 0
            while (i < B + 2) {
                p[B + i] = p[i]
                g1[B + i] = g1[i]
                j = 0
                while (j < 2) {
                    g2[B + i][j] = g2[i][j]
                    j++
                }
                j = 0
                while (j < 3) {
                    g3[B + i][j] = g3[i][j]
                    j++
                }
                i++
            }
        }
    }
}