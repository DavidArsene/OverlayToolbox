package ro.davidarsene.overlaytoolbox.model

import ro.davidarsene.overlaytoolbox.*
import ro.davidarsene.overlaytoolbox.trash.AppInfo
import ro.davidarsene.overlaytoolbox.util.RootHelper
import android.app.Application
import android.content.om.OverlayInfo
import android.os.Process
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData


@Suppress("NewApi")
class MainViewModel(val app: Application) : AndroidViewModel(app) {

    val appList = MutableLiveData<List<AppInfo>>()

    var selectedFilterId = R.id.menu_item_all

    val overlays: Map<String, MutableList<OverlayInfo>>
        get() = RootHelper.overlayManager.getAllOverlays(Process.myUserHandle().identifier)

    fun selectedFilter(o: OverlayInfo) = when (selectedFilterId) {
        R.id.menu_item_mine -> o.packageName in arrayOf(
            app.resources.getString(R.string.settings_overlay_creator), PACKAGE_SHELL, app.packageName
        )
        R.id.menu_item_relevant -> o.overlayName != "SemWT_${o.targetPackageName}" &&
            !o.baseCodePath.contains("current_locale_apks")
        else -> true
    }

    init {
        runOnThread { appList.postValue(getInstalledApps()) }
    }

    private fun getInstalledApps() = app.packageManager.let { pm ->
        val importantApps = arrayOf(PACKAGE_SYSTEM, PACKAGE_SYSTEMUI, app.packageName)

        pm.getInstalledApplications(0)
            .map {
                AppInfo(it.packageName, it.loadLabel(pm).toString(), it.loadIcon(pm))
            }.let { list ->
                if (app.resources.getBoolean(R.bool.settings_sort_app_list))
                    list.sortedBy { it.label }.sortedByDescending {
                        it.packageName in importantApps
                    }
                else list
            }
    }
}
