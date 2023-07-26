package ro.davidarsene.leitmotif

import android.content.om.OverlayInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ro.davidarsene.leitmotif.databinding.ActivityOverlayDetailsBinding

class OverlayDetailsActivity : AppCompatActivity() {

    private lateinit var ui: ActivityOverlayDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityOverlayDetailsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val overlay = RootHelper.overlayManager.getOverlayInfoByIdentifier(
            intent.getParcelableExtra("identifier"), 0)

        ui.overlayDetails.text = """
            Overlay details:
            
            packageName: ${overlay.packageName}
            overlayName: ${overlay.overlayName}
            targetPackageName: ${overlay.targetPackageName}
            targetOverlayableName: ${overlay.targetOverlayableName}
            category: ${overlay.category}
            baseCodePath: ${overlay.baseCodePath}
            state: ${OverlayInfo.stateToString(overlay.state)}
            userId: ${overlay.userId}
            priority: ${overlay.priority}
            isMutable: ${overlay.isMutable}
            isFabricated: ${overlay.isFabricated}
        """.trimIndent()
    }
}
