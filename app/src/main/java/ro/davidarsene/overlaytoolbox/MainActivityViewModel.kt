package ro.davidarsene.overlaytoolbox

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ro.davidarsene.overlaytoolbox.trash.AppInfo

class MainActivityViewModel(private val app: Application) : AndroidViewModel(app) {

    val appList = MutableLiveData<List<AppInfo>>()

    init {
        Thread {
            appList.postValue(getInstalledApps())
        }.start()
    }

    @Suppress("Deprecation")
    private fun getInstalledApps() = app.packageManager.run {

        getInstalledApplications(0)
            .map {
                val label = getApplicationLabel(it).toString()
                val icon = getApplicationIcon(it)
                AppInfo(label, it.packageName, icon)

            }.let { list ->
                if (app.resources.getBoolean(R.bool.sortAppList))
                    list.sortedBy { it.label } else list
            }
    }

}
