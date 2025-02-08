package ro.davidarsene.overlaytoolbox

import android.content.*
import android.content.om.OverlayInfo
import android.content.om.OverlayManagerTransaction
import android.os.Bundle
import android.os.Process
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ro.davidarsene.overlaytoolbox.databinding.ActivityDetailsBinding
import ro.davidarsene.overlaytoolbox.databinding.ItemDetailBinding
import ro.davidarsene.overlaytoolbox.model.DetailsViewModel
import ro.davidarsene.overlaytoolbox.trash.LazyAppInfo
import ro.davidarsene.overlaytoolbox.trash.ResourceAdapter
import ro.davidarsene.overlaytoolbox.util.RootHelper
import java.io.File


@Suppress("Deprecation", "NewApi")
class DetailsActivity : AppCompatActivity() {

    private val vm: DetailsViewModel by viewModels()
    private lateinit var overlay: OverlayInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ui = ActivityDetailsBinding.inflate(layoutInflater)
        // Needed because activity_main has two different roots (LinearLayout and GridLayout)
        val root: View = ui.root // otherwise this call would be getRoot()Landroid/widget/LinerLayout;
        setContentView(root)     // instead of getRoot()Landroid/view/View;

        overlay = RootHelper.overlayManager.getOverlayInfoByIdentifier(
            intent.getParcelableExtra(INTENT_IDENTIFIER), Process.myUserHandle().identifier)
        val overlay = overlay

        ui.toolbar.title = overlay.shortName()
        setSupportActionBar(ui.toolbar)

//            targetOverlayableName: ${overlay.targetOverlayableName}
//            userId: ${overlay.userId}

        ui.enable.isChecked = overlay.isEnabled
        ui.enable.isEnabled = overlay.isMutable
        ui.enable.setOnCheckedChangeListener { _, isChecked ->
            OverlayManagerTransaction.Builder()
                .setEnabled(overlay.overlayIdentifier, isChecked)
                .commit()
        }

        if (overlay.category != null) showDetail(R.string.detail_category, overlay.category, ui.category)

        if (overlay.state != OverlayInfo.STATE_ENABLED &&
            overlay.state != OverlayInfo.STATE_DISABLED) {

            showDetail(R.string.detail_state, OverlayInfo.stateToString(overlay.state), ui.error)
            ui.error.content.setTextColor(getColor(com.google.android.material.R.color.design_error))
            ui.enable.isEnabled = false
        }

        showDetail(R.string.detail_target, null, ui.target)
        LazyAppInfo(overlay.targetPackageName).display(ui.targetItem)

        if (overlay.isFabricated) {
            showDetail(R.string.detail_source, null, ui.source)
            LazyAppInfo(overlay.packageName).display(ui.sourceItem)
            ui.sourceItem.root.visibility = View.VISIBLE

        } else {
            showDetail(R.string.detail_path, overlay.baseCodePath, ui.path)

            ui.path.root.setOnClickListener {
               Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(
                        File(overlay.baseCodePath).toUri(),
                        "application/vnd.android.package-archive"
                    )
                }.also {
                   try {
                       startActivity(it)
                   } catch (_: Exception) {
                       toast(R.string.no_apk_viewer)
                   }
                }
            }
        }

        showDetail(
            R.string.detail_priority,
            when (overlay.priority) {
                Int.MAX_VALUE -> getString(R.string.detail_priority_highest)
                0 -> getString(R.string.detail_priority_lowest)
                else -> overlay.priority.toString()
            },
            ui.priority
        )

        showDetail(R.string.detail_resources, null, ui.resources)

        if (!vm.overlaidResources.isInitialized) vm.loadResources(overlay)
        vm.overlaidResources.observe(this) { overlaidResources ->
            ui.loading.hide()
            ui.resourceList.adapter = ResourceAdapter(overlaidResources) {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText(
                    it.name,
                    "${it.name}\n${it.old ?: ""}\n${it.new}"
                ))
                toast(R.string.toast_clipboard)
            }
            ui.resourceListContainer.visibility = View.VISIBLE
        }
    }

    private fun showDetail(title: Int, content: String?, item: ItemDetailBinding) {
        item.title.text = getString(title)
        item.root.visibility = View.VISIBLE
        if (content != null) item.content.text = content
        else item.content.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (overlay.isFabricated) menuInflater.inflate(R.menu.details_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.details_bar_menu_share -> {

                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, overlay.baseCodePath.toUri())
                    type = "*/*"
                }.also { startActivity(Intent.createChooser(it, null)) }
                true
            }
            R.id.details_bar_menu_delete -> {

                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.delete_overlay_title)
                    .setPositiveButton(R.string.details_bar_menu_delete) { _, _ ->

                        OverlayManagerTransaction.Builder()
                            .unregisterFabricatedOverlay(overlay.overlayIdentifier)
                            .commit()

                        finish()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
