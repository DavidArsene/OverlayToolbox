package ro.davidarsene.overlaytoolbox.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ro.davidarsene.overlaytoolbox.databinding.SearchableListBinding
import ro.davidarsene.overlaytoolbox.trash.BaseAdapter

object SearchableListDialog {

    fun show(context: Context, adapter: BaseAdapter<*, *>): AlertDialog {

        val ui = SearchableListBinding.inflate(LayoutInflater.from(context))

        ui.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        ui.list.adapter = adapter
        adapter.filter.filter(null)

        return MaterialAlertDialogBuilder(context).setView(ui.root).show().also {
            it.window!!.setLayout(MATCH_PARENT, MATCH_PARENT)
        }
    }
}
