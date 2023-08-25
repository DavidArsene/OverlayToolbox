package ro.davidarsene.leitmotif

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ro.davidarsene.leitmotif.databinding.*
import ro.davidarsene.leitmotif.trash.*


class MainActivity : AppCompatActivity() {

    private lateinit var ui: ActivityMainBinding
    private val vm: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        ui.fab.setOnClickListener { newOverlayFabListener() }
        vm.appList.observe(this) { ui.fab.show() }

        RootHelper.ready.observe(this) { showOverlays() }
    }

    private fun showOverlays() {
        val overlays = RootHelper.overlayManager.getAllOverlays(0)

        ui.overlayList.adapter = object :
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1) {
            override fun getCount(): Int = overlays.size
            override fun getItem(position: Int): String = overlays.keys.elementAt(position)
        }

        ui.overlayList.setOnItemClickListener { _, _, position, _ ->
            val overlaysForPackage = overlays.keys.elementAt(position)

            MaterialAlertDialogBuilder(this)
                .setTitle("Choose overlay for $overlaysForPackage")
                .setItems(
                    overlays[overlaysForPackage]!!.map { it.fullName() }.toTypedArray()) { _, which ->

                    val identifier = overlays[overlaysForPackage]!![which].overlayIdentifier

                    val intent = Intent(this, OverlayDetailsActivity::class.java)
                    intent.putExtra("identifier", identifier)
                    startActivity(intent)

                }.show()
        }
    }

    private fun newOverlayFabListener() {
        val dialogUi = AppChooserBinding.inflate(layoutInflater)

        dialogUi.appList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        dialogUi.appList.adapter = AppChooserAdapter(vm.appList.value!!)

        dialogUi.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?) =
                (dialogUi.appList.adapter as AppChooserAdapter).filter.filter(newText).let { true }
        })

        MaterialAlertDialogBuilder(this).setView(dialogUi.root).show().apply {
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
}
