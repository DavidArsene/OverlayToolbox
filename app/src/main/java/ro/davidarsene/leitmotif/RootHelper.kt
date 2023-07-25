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
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.util.concurrent.CountDownLatch

object RootHelper {

    private val TAG = this::class.java.simpleName
    private val latch = CountDownLatch(1)

    lateinit var overlayManager: IOverlayManager

    fun init(context: Context) {
        Log.d(TAG, "Starting root service")

        HiddenApiBypass.setHiddenApiExemptions("")

        val intent = Intent(context, RootBridge::class.java)
        RootService.bind(intent, Connection())

        Thread {
            latch.await()

            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)

        }.start()
    }

    private class Connection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val rootBridge = IRootBridge.Stub.asInterface(service)
            overlayManager = IOverlayManager.Stub.asInterface(rootBridge.overlayManager)

            Log.d(TAG, "Root service started")
            latch.countDown()
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
