package ro.davidarsene.overlaytoolbox.trash

import android.app.AppGlobals
import android.content.pm.PackageManager
import ro.davidarsene.overlaytoolbox.databinding.ItemAppBinding


class OverlayListAdapter(
    onItemClicked: (LazyAppInfo) -> Unit,
    private val extraFilter: (LazyAppInfo) -> Boolean
) :
    BaseAdapter<LazyAppInfo, ItemAppBinding>(
        null, ItemAppBinding::inflate, onItemClicked
    ) {

    override fun onBindViewHolder(item: LazyAppInfo, ui: ItemAppBinding) = item.displayClickable(ui)

    override fun filter(item: LazyAppInfo, constraint: CharSequence) = extraFilter(item)
}

class LazyAppInfo(val packageName: String) {

    companion object { val pm = AppGlobals.getInitialApplication().packageManager!! }

    private val app by lazy {
        try {
            val info = pm.getApplicationInfo(packageName, 0)

            // The undocumented load*() methods don't unnecessarily call getApplicationInfo again.
            AppInfo(packageName, info.loadLabel(pm).toString(), info.loadIcon(pm))

        } catch (e: PackageManager.NameNotFoundException) { null }
    }

    fun display(ui: ItemAppBinding) {
        ui.root.background = null
        displayClickable(ui)
    }

    fun displayClickable(ui: ItemAppBinding) {
        val app = app
        if (app != null) {
            ui.label.text = app.label
            ui.packageName.text = packageName
            ui.icon.setImageDrawable(app.icon)
        } else {
            ui.label.text = packageName
            ui.packageName.text = null
            ui.icon.setImageDrawable(null)
        }
    }

    override fun equals(other: Any?) = when(other) {
        is LazyAppInfo -> packageName == other.packageName
        else -> false
    }

    override fun hashCode() = packageName.hashCode()
}
