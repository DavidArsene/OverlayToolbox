package ro.davidarsene.overlaytoolbox

import ro.davidarsene.overlaytoolbox.databinding.ActivityMainBinding
import ro.davidarsene.overlaytoolbox.databinding.OverlaySheetBinding
import ro.davidarsene.overlaytoolbox.model.MainViewModel
import ro.davidarsene.overlaytoolbox.trash.*
import ro.davidarsene.overlaytoolbox.util.RootHelper
import ro.davidarsene.overlaytoolbox.util.SearchableListDialog
import android.content.om.OverlayInfo
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialog


@Suppress("NewApi")
class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private lateinit var ui: ActivityMainBinding
    private lateinit var overlayList: Map<LazyAppInfo, List<OverlayInfo>>
    private val vm: MainViewModel by viewModels()

    private var appChooserDialog: AlertDialog? = null
    private lateinit var appChooserAdapter: AppChooserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityMainBinding.inflate(layoutInflater)
        val ui = ui
        setContentView(ui.root)
        setSupportActionBar(ui.toolbar)

        ui.swipeRefresh.setOnRefreshListener(this)
        ui.swipeRefresh.isRefreshing = true

        ui.fab.setOnClickListener(this)
        vm.appList.observe(this) {
            appChooserAdapter = AppChooserAdapter(it) { item ->
                startActivity(CreatorActivity::class.java) {
                    putExtra(INTENT_TARGET_PACKAGE, item.packageName)
                }
                appChooserDialog!!.dismiss()
                appChooserDialog = null
            }
            ui.fab.show()
        }

        ui.overlayList.adapter = OverlayListAdapter({ item ->
            val overlays = overlayList[item]!!.filter { vm.selectedFilter(it) }

            BottomSheetDialog(this).apply {
                val ui = OverlaySheetBinding.inflate(layoutInflater)
                setContentView(ui.root)
                item.display(ui.app)

                ui.overlayList.adapter = object : ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    overlays.map { it.fullName() }
                ) {
                    override fun getCount() = overlays.size
                    override fun getItem(position: Int) = overlays[position].fullName()
                }

                ui.overlayList.setOnItemClickListener { _, _, which, _ ->
                    startActivity(DetailsActivity::class.java) {
                        putExtra(INTENT_IDENTIFIER, overlays[which].overlayIdentifier)
                    }
                    dismiss()
                }
                show()
            }
        }, { item -> overlayList[item]!!.any { vm.selectedFilter(it) } })
    }

    override fun onClick(v: View) {
        appChooserDialog = SearchableListDialog.show(this, appChooserAdapter)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onRefresh() {
        if (RootHelper.ready.value == true) loadOverlays()
        else RootHelper.ready.observe(this) { loadOverlays() }
    }

    private fun loadOverlays() = runOnThread {
        val overlays = vm.overlays.mapKeys { LazyAppInfo(it.key) }

        runOnUiThread {
            overlayList = overlays
            (ui.overlayList.adapter as OverlayListAdapter).apply {
                list = overlays.keys.toList().sortedBy { it.packageName }
                filter.filter(" ")
            }
            ui.swipeRefresh.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_bar, menu)
        menu.findItem(vm.selectedFilterId).isChecked = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        vm.selectedFilterId = item.itemId

        (ui.overlayList.adapter as OverlayListAdapter).filter.filter(" ")
        item.isChecked = true
        return true
    }
}
