package ro.davidarsene.leitmotif

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.om.IOverlayManager
import android.os.IBinder
import android.os.ServiceManager
import android.util.Log
import com.topjohnwu.superuser.ipc.RootService

object RootHelper {
    private val TAG = this::class.java.simpleName

    lateinit var overlayManager: IOverlayManager

    fun init(context: Context) {
        Log.d(TAG, "Starting root service")
        val intent = Intent(context, RootBridge::class.java)
        RootService.bind(intent, Connection())
    }

    private class Connection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val rootService = IRootBridge.Stub.asInterface(service)
            overlayManager = IOverlayManager.Stub.asInterface(rootService.overlayManager)

            Log.d(TAG, "Root service started")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d(TAG, "Stopping root service")
        }
    }

    private class RootBridge : RootService() {
        override fun onBind(intent: Intent): IBinder =

            object : IRootBridge.Stub() {

                override fun getOverlayManager(): IBinder =
                    ServiceManager.getService("overlay")
            }
    }
}