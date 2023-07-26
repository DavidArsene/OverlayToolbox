package ro.davidarsene.leitmotif

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean =
        menuInflater.inflate(R.menu.menu_main, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
}
