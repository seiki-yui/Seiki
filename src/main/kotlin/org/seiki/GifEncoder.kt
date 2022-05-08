package org.seiki

import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.Closeable
import java.io.File
import java.io.IOException
import javax.imageio.*
import javax.imageio.metadata.IIOInvalidTreeException
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.FileImageOutputStream
import javax.imageio.stream.ImageOutputStream

@Suppress("KotlinConstantConditions", "SameParameterValue")
class GifEncoder private constructor(outputStream: ImageOutputStream, imageType: Int, delay: Int, loop: Boolean) :
    Closeable {
    private val writer: ImageWriter = ImageIO.getImageWritersBySuffix("gif").next()
    private val params: ImageWriteParam = writer.defaultWriteParam
    private val metadata: IIOMetadata

    @Throws(IIOInvalidTreeException::class)
    private fun configureRootMetadata(delay: Int, loop: Boolean) {
        val metaFormatName = metadata.nativeMetadataFormatName
        val root = metadata.getAsTree(metaFormatName) as IIOMetadataNode
        getNode(root, "GraphicControlExtension").apply {
            setAttribute("disposalMethod", "none")
            setAttribute("userInputFlag", "FALSE")
            setAttribute("transparentColorFlag", "FALSE")
            setAttribute("delayTime", (delay / 10).toString())
            setAttribute("transparentColorIndex", "0")
        }
        IIOMetadataNode("ApplicationExtension").apply {
            setAttribute("applicationID", "NETSCAPE")
            setAttribute("authenticationCode", "2.0")
            val loopContinuously = if (loop) 0 else 1
            userObject =
                byteArrayOf(0x1, (loopContinuously and 0xFF).toByte(), (loopContinuously shr 8 and 0xFF).toByte())
            getNode(root, "ApplicationExtensions").appendChild(this)
        }
        metadata.setFromTree(metaFormatName, root)
    }

    @Throws(IOException::class)
    fun writeToSequence(img: RenderedImage) {
        writer.writeToSequence(IIOImage(img, null, metadata), params)
    }

    @Throws(IOException::class)
    override fun close() {
        writer.endWriteSequence()
    }

    companion object {
        private fun getNode(rootNode: IIOMetadataNode, nodeName: String): IIOMetadataNode {
            val nNodes = rootNode.length
            for (i in 0 until nNodes) {
                if (rootNode.item(i).nodeName.equals(nodeName, ignoreCase = true)) {
                    return rootNode.item(i) as IIOMetadataNode
                }
            }
            IIOMetadataNode(nodeName).apply {
                rootNode.appendChild(this)
                return this
            }
        }

        private fun convert(
            images: Array<BufferedImage>,
            outputStream: ImageOutputStream,
            delay: Int,
            loop: Boolean,
            width: Int?,
            height: Int?
        ) {
            //图像类型
            val imageType = images[0].type
            //缩放参数
            val sx = if (width == null) 1.0 else width.toDouble() / images[0].width
            val sy = if (height == null) 1.0 else height.toDouble() / images[0].height
            val op = AffineTransformOp(AffineTransform.getScaleInstance(sx, sy), null)
            runCatching {
                outputStream.use { output ->
                    GifEncoder(output, imageType, delay, loop).use { gif ->
                        for (image in images) {
                            gif.writeToSequence(op.filter(image, null))
                        }
                    }
                }
            }.onFailure { e -> throw e }
        }
        /**
         * convert Jpeg to Gif
         * Currently, the PNG is not support
         *
         * 将Jpeg转换为Gif图片
         * 暂不支持PNG图片
         * @param images: 传入的图片，有顺序
         * @param gifPath: 输出路径
         * @param delay: 每张图片的切换间隔
         * @param loop: 是否循环播放
         *
         * */
        fun convert(
            images: Array<BufferedImage>,
            gifPath: String,
            delay: Int,
            loop: Boolean = true,
            width: Int? = null,
            height: Int? = null
        ) {
            val fileImageOutputStream = FileImageOutputStream(File(gifPath))
            convert(images, fileImageOutputStream, delay, loop, width, height)
        }
    }

    init {
        metadata = writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromBufferedImageType(imageType), params)
        //配置元数据
        configureRootMetadata(delay, loop)
        //设置输出流
        writer.output = outputStream
        writer.prepareWriteSequence(null)
    }
}