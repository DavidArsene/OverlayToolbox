package ro.davidarsene.overlaytoolbox.util

import android.content.pm.PackageManager
import android.idmap2.pb.FabricatedV1
import com.google.devrel.gmscore.tools.apk.arsc.*
import ro.davidarsene.overlaytoolbox.trash.LazyAppInfo
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder


object Parsers {

    fun arsc(targetPackage: String) = try {
        arscFromApk(LazyAppInfo.pm.getApplicationInfo(targetPackage, 0).sourceDir)
    } catch (e: PackageManager.NameNotFoundException) { null }

    fun arscFromApk(apkFile: String) = ArscUtils.getResources(File(apkFile)).typeChunks

    fun proto(inputStream: InputStream): ParsedFrro {

        val buf = ByteBuffer.wrap(inputStream.readBytes()).order(ByteOrder.LITTLE_ENDIAN)
        inputStream.close()

        val magic = buf.getInt()
        val fabricatedOverlayMagic = 0x4f525246 // "FRRO"
        if (magic != fabricatedOverlayMagic) throw IOException("Invalid magic: $magic")

        val version = buf.getInt()
        if (version !in 1..3) throw IOException("Unsupported version: $version")

        /* val crc = */buf.getInt()

        if (version >= 3) {
            val totalBinaryBytes = buf.getInt()
            buf.position(buf.position() + totalBinaryBytes)
        }

        val stringPool = if (version >= 2) {
            /* val stringPoolSize = */buf.getInt()
            Chunk.newInstance(buf, null) as StringPoolChunk
        } else null

        val frro = FabricatedV1.FabricatedOverlay.parseFrom(buf)
        return ParsedFrro(frro.getPackages(0), frro.name, stringPool)
    }

    class ParsedFrro(
        val frro: FabricatedV1.ResourcePackage,
        val name: String,
        val stringPool: StringPoolChunk?
    )
}

