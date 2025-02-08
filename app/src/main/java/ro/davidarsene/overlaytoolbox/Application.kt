package ro.davidarsene.overlaytoolbox

import android.os.StrictMode
import ro.davidarsene.overlaytoolbox.util.RootHelper

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        // Allow file:// URIs to be exposed to other apps
        val lenientPolicy = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(lenientPolicy.build())

        RootHelper.init(this)
    }
}
