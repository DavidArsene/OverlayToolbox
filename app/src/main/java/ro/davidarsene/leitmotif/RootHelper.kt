package ro.davidarsene.leitmotif

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.om.IOverlayManager
import android.os.IBinder
import android.os.ServiceManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.topjohnwu.superuser.ipc.RootService
import org.lsposed.hiddenapibypass.HiddenApiBypass

object RootHelper {

    private val TAG = this::class.java.simpleName
    val ready = MutableLiveData<Unit>()

    lateinit var overlayManager: IOverlayManager

    fun init(context: Context) {
        Log.d(TAG, "Starting root service")

        HiddenApiBypass.setHiddenApiExemptions("")

        val intent = Intent(context, RootBridge::class.java)
        RootService.bind(intent, Connection())
    }

    private class Connection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val rootBridge = IRootBridge.Stub.asInterface(service)

            overlayManager = IOverlayManager.Stub.asInterface(
                rootBridge.getService(Context.OVERLAY_SERVICE))

            Log.d(TAG, "Root service started")
            ready.value = Unit
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Stopping root service")
        }
    }

    private class RootBridge : RootService() {
        override fun onBind(intent: Intent): IBinder =

            object : IRootBridge.Stub() {

                override fun getService(name: String?): IBinder =
                    ServiceManager.getService(name)
            }
    }
}
