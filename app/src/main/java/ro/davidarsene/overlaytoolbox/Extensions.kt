@file:Suppress("NewApi")

package ro.davidarsene.overlaytoolbox

import ro.davidarsene.overlaytoolbox.util.RootHelper
import android.content.Context
import android.content.Intent
import android.content.om.OverlayInfo
import android.content.om.OverlayManagerTransaction
import android.idmap2.pb.FabricatedV1
import android.widget.Toast
import com.google.devrel.gmscore.tools.apk.arsc.TypeChunk

fun OverlayInfo.fullName(): String = if (isFabricated) "$packageName:$overlayName" else packageName
fun OverlayInfo.shortName(): String = if (isFabricated) overlayName else packageName

fun OverlayManagerTransaction.Builder.commit() = RootHelper.commit(build())

fun FabricatedV1.ResourceEntry.fullName(type: FabricatedV1.ResourceType) = "${type.name}/$name"

fun TypeChunk.Entry.fullName() = "$typeName/$key"

fun TypeChunk.Entry.valueToString() = value?.toString() ?: """["${values.values.joinToString("\", \"")}"]"""

fun runOnThread(action: Runnable) = Thread(action).start()

fun Context.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, resId, duration).show()

fun Context.startActivity(cls: Class<*>, block: Intent.() -> Unit) = Intent(this, cls)
    .apply(block)
    .also { startActivity(it) }
