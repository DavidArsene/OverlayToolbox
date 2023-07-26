package ro.davidarsene.leitmotif

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ro.davidarsene.leitmotif.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var ui: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)
        setSupportActionBar(ui.toolbar)

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
                    overlays[overlaysForPackage]!!.map {
                        it.packageName + if (it.isFabricated) ":${it.overlayName}" else ""
                    }.toTypedArray()) { _, which ->

                    val identifier = overlays[overlaysForPackage]!![which].overlayIdentifier

                    val intent = Intent(this, OverlayDetailsActivity::class.java)
                    intent.putExtra("identifier", identifier)
                    startActivity(intent)

                }.show()
        }
    }
}
