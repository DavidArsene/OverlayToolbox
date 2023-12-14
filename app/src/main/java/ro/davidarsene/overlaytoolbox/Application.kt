package ro.davidarsene.overlaytoolbox

import ro.davidarsene.overlaytoolbox.trash.LazyAppInfo
import ro.davidarsene.overlaytoolbox.util.RootHelper
import android.os.StrictMode

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        // Allow file:// URIs to be exposed to other apps
        val lenientPolicy = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(lenientPolicy.build())

        LazyAppInfo.pm = packageManager

        RootHelper.init(this)
    }
}
