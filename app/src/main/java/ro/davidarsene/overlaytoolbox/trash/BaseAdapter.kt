package ro.davidarsene.overlaytoolbox.trash

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.*
import androidx.viewbinding.ViewBinding


abstract class BaseAdapter<T : Any, V : ViewBinding>(
    var list: List<T>? = null,
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> V,
    private val onItemClicked: (T) -> Unit
) : ListAdapter<T, BaseAdapter<T, V>.ViewHolder>(DiffCallback<T>()), Filterable {

    init { this.submitList(list) }

    inner class ViewHolder(val ui: V) : RecyclerView.ViewHolder(ui.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        bindingInflater(LayoutInflater.from(parent.context), parent, false)
    ).apply {
        itemView.setOnClickListener {
            val pos = bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) onItemClicked(getItem(pos))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        onBindViewHolder(getItem(position), holder.ui)

    abstract fun onBindViewHolder(item: T, ui: V)

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?) = FilterResults().apply {

            values = if (constraint.isNullOrEmpty()) list
            else list?.filter { filter(it, constraint) }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) =
            submitList(results?.values as List<T>?)
    }

    open fun filter(item: T, constraint: CharSequence): Boolean = true

    private class DiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem

        override fun areContentsTheSame(oldItem: T, newItem: T) = true
    }
}
