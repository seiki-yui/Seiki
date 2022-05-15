@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.seiki

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.utils.info
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.seiki.plugin.SeikiMain
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

object SweetBoy {
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    /**
     * okhttp GET请求
     * @param url URL链接
     * @return okhttp给我返回了什么我就返回什么捏😋
     */
    suspend fun get(url: String): Response =
        withContext(Dispatchers.IO) {
            okHttpClient.newCall(
                Request.Builder().url(url).build()
            ).execute()
        }

    /**
     * okhttp POST请求
     * @param url URL链接
     * @param hashMap POST的json的HashMap
     * @return okhttp给我返回了什么我就返回什么捏😋
     */
    suspend fun post(url: String, hashMap: HashMap<String, String> = HashMap()): Response =
        withContext(Dispatchers.IO) {
            val builder = FormBody.Builder()
            for (key in hashMap.keys) {
                builder.add(key, hashMap[key]!!)
                SeikiMain.logger.info { "$key : ${hashMap[key]!!}" }
            }
            val formBody = builder.build()
            return@withContext okHttpClient.newCall(
                Request.Builder()
                    .method("POST", formBody)
                    .url(url)
                    .build()
            ).execute()
        }

    /**
     * okhttp 下载
     * @param url 文件URL
     * @param path 储存文件的位置
     */
    suspend fun getFile(url: String, path: String): File =
        File(path).apply {
            this.outputStream().apply {
                write(get(url).body!!.byteStream().readBytes())
                flush()
                close()
            }
        }

    suspend fun getStream(url: String) = get(url).body!!.byteStream()

    suspend fun getBytes(url: String) = getStream(url).readBytes()

    /**
     * 根据文件夹路径与后缀名来检索
     * @param path 文件夹路径
     * @param ext 储存后缀的列表
     * @param whetherFolder 是否检索子文件夹
     * @return 文件列表
     */
    @Deprecated("这个不好用 不如自己现成造", level = DeprecationLevel.WARNING)
    fun findFileByExt(path: String, ext: List<String>, whetherFolder: Boolean? = true): MutableList<String> {
        val fileNames: MutableList<String> = mutableListOf()
        val fileTree: FileTreeWalk = File(path).walk()
        if (whetherFolder != null && whetherFolder)
            fileTree.maxDepth(1).filter { it.isFile }.filter { it.extension in ext }.forEach { fileNames.add(it.name) }
        else fileTree.filter { it.extension in ext }.forEach { fileNames.add(it.name) }
        return fileNames
    }

    /**
     * 时间戳转字符串
     * @return YY-MM-DD hh:mm:ss
     */
    fun Number.transToTime(): String = this.transToTime("yyyy-MM-dd HH:mm:ss")

    /**
     * 时间戳转字符串
     * @param pattern 模板字符串
     * @return 转换后的时间捏😋
     */
    fun Number.transToTime(pattern: String): String = SimpleDateFormat(pattern).format(this)

    /**
     * 数量转中文
     * @return 哈哈
     */
    fun Number.transToNum(): String = when (this) {
        in 0..10000 -> this.toString()
        in 10000..100000000 -> "${this.toLong() / 10000}万"
        in 100000000..1000000000000 -> "${this.toLong() / 100000000}亿"
        else -> this.toString()
    }

    /**
     * 数量转中文 同[Number.transToNum],但是加入保留小数位数。
     * @param number 保留的小数位数
     * @return 哈哈
     */
    fun Number.transToNum(number: Number): String = when (this) {
        in 0..10000 -> this.toString()
        in 10000..100000000 -> "${String.format("%.${number}f", (this.toLong() / 10000.0))}万"
        in 100000000..1000000000000 -> "${String.format("%.${number}f", (this.toLong() / 100000000.0))}亿"
        else -> this.toString()
    }

    /**
     * 匹配正则表达式的括号,返回匹配到的Hash,如果没有返回null.
     * @param regex 正则表达式
     * @return List
     */
    fun String.matchRegex(regex: Regex) = if (regex.matches(this)) {
        regex.findAll(this).toList().flatMap(MatchResult::groupValues)
    } else null

    /**
     * 同[String.matchRegex],但是如果没有匹配到的括号就抛出异常。
     * @param regex 正则表达式
     * @throws NoSuchElementException
     * @return List
     */
    fun String.matchRegexOrFail(regex: Regex) = if (regex.matches(this)) {
        regex.findAll(this).toList().flatMap(MatchResult::groupValues)
    } else throw NoSuchElementException("字符串没有匹配到正则表达式")

    /**
     * 匹配正则表达式的括号,并运行块,参数为获取到的Hash
     * @param regex 正则表达式
     * @param action Lambda块
     * @return List
     */
    suspend fun <R> String.matchRegexRun(regex: Regex, action: suspend (List<String>) -> R): R? =
        this.matchRegex(regex)?.let { action(it) }

    /**
     * 同[String.matchRegexRun],但是如果没有匹配到的括号就抛出异常。
     * @param regex 正则表达式
     * @param action Lambda块
     * @throws NoSuchElementException
     * @return List
     */
    suspend fun <R> String.matchRegexRunOrFail(regex: Regex, action: suspend (List<String>) -> R): R? =
        action(this.matchRegexOrFail(regex))
}