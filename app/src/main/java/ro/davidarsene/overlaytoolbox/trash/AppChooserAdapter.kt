package ro.davidarsene.overlaytoolbox.trash

import ro.davidarsene.overlaytoolbox.databinding.ItemAppBinding
import android.graphics.drawable.Drawable
import com.l4digital.fastscroll.FastScroller


data class AppInfo(val packageName: String, val label: String, val icon: Drawable)

class AppChooserAdapter(appList: List<AppInfo>, onItemClicked: (AppInfo) -> Unit) :
    BaseAdapter<AppInfo, ItemAppBinding>(
        appList, ItemAppBinding::inflate, onItemClicked
    ), FastScroller.SectionIndexer {

    override fun onBindViewHolder(item: AppInfo, ui: ItemAppBinding) {
        ui.label.text = item.label
        ui.packageName.text = item.packageName
        ui.icon.setImageDrawable(item.icon)
    }

    override fun filter(item: AppInfo, constraint: CharSequence) =
        item.label.contains(constraint, true) ||
                item.packageName.contains(constraint, true)

    override fun getSectionText(position: Int) = getItem(position).label[0].toString()
}
