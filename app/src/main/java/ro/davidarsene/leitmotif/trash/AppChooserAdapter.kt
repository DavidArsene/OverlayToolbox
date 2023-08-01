package ro.davidarsene.leitmotif.trash

import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.*
import com.l4digital.fastscroll.FastScroller
import ro.davidarsene.leitmotif.*
import ro.davidarsene.leitmotif.databinding.AppChooserItemBinding


class AppChooserAdapter(private val appList: List<AppInfo>) :
    ListAdapter<AppInfo, AppChooserAdapter.ViewHolder>(DiffCallback()),
    Filterable, FastScroller.SectionIndexer {

    init { submitList(appList) }

    inner class ViewHolder(val ui: AppChooserItemBinding) : RecyclerView.ViewHolder(ui.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        AppChooserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = getItem(position)
        holder.ui.apply {
            label.text = appInfo.label
            packageName.text = appInfo.packageName
            icon.setImageDrawable(appInfo.icon)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, OverlayCreatorActivity::class.java)
            intent.putExtra("packageName", appInfo.packageName)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?) = FilterResults().apply {

            values = if (constraint.isNullOrEmpty()) appList else appList.filter {
                it.label.contains(constraint, true) || it.packageName.contains(constraint, true)
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) =
            submitList(results?.values as List<AppInfo>?)
    }

    override fun getSectionText(position: Int) = getItem(position).label[0].toString()
}

data class AppInfo(val label: String, val packageName: String, val icon: Drawable)

private class DiffCallback : DiffUtil.ItemCallback<AppInfo>() {
    override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo) = oldItem.packageName == newItem.packageName

    override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo) = oldItem == newItem
}
