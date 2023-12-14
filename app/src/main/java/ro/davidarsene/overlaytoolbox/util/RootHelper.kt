package ro.davidarsene.overlaytoolbox.util

import org.lsposed.hiddenapibypass.HiddenApiBypass
import ro.davidarsene.overlaytoolbox.IRootBridge
import android.content.*
import android.content.om.IOverlayManager
import android.content.om.OverlayManagerTransaction
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.ServiceManager
import androidx.lifecycle.MutableLiveData
import com.topjohnwu.superuser.ipc.RootService

@Suppress("InlinedApi")
object RootHelper {

    val ready = MutableLiveData<Boolean>()

    lateinit var overlayManager: IOverlayManager
    lateinit var commit: (OverlayManagerTransaction) -> Unit

    fun init(context: Context) = with(context) {
        HiddenApiBypass.setHiddenApiExemptions("")

        // Place the app inside /system for faster startup
        if (isSystemApp(this)) {

            // A14+ only exposes a limited number of overlay APIs.
            // IOverlayManager still requires root access or system.
            overlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE))

            ready.value = true

            // commit additionally requires the CHANGE_OVERLAY_PACKAGES permission,
            // which needs to be declared in /system/etc/permissions.
            if (checkSelfPermission(android.Manifest.permission.CHANGE_OVERLAY_PACKAGES)
                == PackageManager.PERMISSION_GRANTED
            ) {

                commit = { overlayManager.commit(it) }
                return
            }
        }

        val intent = Intent(context, RootBridge::class.java)
        RootService.bind(intent, Service())
    }

    fun isSystemApp(context: Context) = with(context) {
        val info = packageManager.getApplicationInfo(packageName, 0)
        info.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    private class Service : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val rootBridge = IRootBridge.Stub.asInterface(service)

            commit = { rootBridge.commit(it) }

            if (ready.value == true) return

            overlayManager = IOverlayManager.Stub.asInterface(
                rootBridge.getService(Context.OVERLAY_SERVICE))

            ready.value = true
        }

        override fun onServiceDisconnected(name: ComponentName?) = Unit
    }

    private class RootBridge : RootService() {
        override fun onBind(intent: Intent) = object : IRootBridge.Stub() {

            override fun getService(name: String?) = ServiceManager.getService(name)

            override fun commit(transaction: OverlayManagerTransaction?) {

                val overlayManager = IOverlayManager.Stub.asInterface(
                    getService(Context.OVERLAY_SERVICE))

                overlayManager.commit(transaction)
            }
        }
    }
}

