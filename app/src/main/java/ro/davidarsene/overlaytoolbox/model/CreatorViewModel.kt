package ro.davidarsene.overlaytoolbox.model

import ro.davidarsene.overlaytoolbox.*
import ro.davidarsene.overlaytoolbox.trash.OverlaidResource
import ro.davidarsene.overlaytoolbox.util.Parsers
import ro.davidarsene.overlaytoolbox.util.RootHelper
import android.app.Application
import android.content.Intent
import android.content.om.FabricatedOverlay
import android.content.om.OverlayManagerTransaction
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.devrel.gmscore.tools.apk.arsc.BinaryResourceValue

class CreatorViewModel(val app: Application) : AndroidViewModel(app) {

    val availableResources = ArrayList<OverlaidResource>()
    val selectedResources = ArrayList<OverlaidResource>()
    lateinit var targetPackage: String
    val state = MutableLiveData<Result>()
    var name: String? = null

    fun createOverlay(bootloopProtection: Boolean): FabricatedOverlay {
        val overlay = FabricatedOverlay.Builder(
            when {
                bootloopProtection -> PACKAGE_SHELL // Android deletes all overlays created by Shell on every boot
                app.resources.getBoolean(R.bool.settings_create_as_self) && RootHelper.isSystemApp(app) -> app.packageName
                else -> app.resources.getString(R.string.settings_overlay_creator) // Overlay creators must be preinstalled apps
            },
            name,
            targetPackage,
        )

        @Suppress("Deprecation") // Use old methods instead of A14 ones to support A12+
        selectedResources.forEach { res ->
            if (res.actual == null) {
                overlay.setResourceValue(res.name, BinaryResourceValue.Type.STRING.code.toInt(), res.new)
            } else {
                overlay.setResourceValue(res.name, res.actual.type.code.toInt(), res.actual.data)
            }
        }

        return overlay.build().also {

            OverlayManagerTransaction.Builder()
                .registerFabricatedOverlay(it)
                .commit()
        }
    }

    fun loadResources(intent: Intent) {
        if (state.isInitialized) return

        var pb: Parsers.ParsedFrro? = null
        targetPackage = intent.getStringExtra(INTENT_TARGET_PACKAGE) ?: run {

            pb = Parsers.proto(app.contentResolver.openInputStream(intent.data!!)!!)
            name = pb!!.name
            pb!!.frro.name
        }

        runOnThread {
            val materialFilter = app.resources.getStringArray(R.array.mdc_resources)
            val types = Parsers.arsc(targetPackage, app.packageManager)!!
            val list = CreatorDialogs.supportedTypes.flatMap { type ->

                val list = (types
                    .firstOrNull { it.typeName == type && it.configuration.isDefault }
                    ?: return@flatMap emptyList())
                    .entries.values.toList()

                if (app.resources.getBoolean(R.bool.settings_hide_mdc_lib)) {
                    list.filter { materialFilter.none { str -> it.key.contains(str) } }
                } else list
            }

            if (list.isEmpty()) {
                return@runOnThread state.postValue(Result.NO_RESOURCES)
            }

            // Fabricated Overlays are based on resource names (not IDs),
            // so any app with duplicated resource names is not supported
            if (list[0].key == list[1].key) {
                return@runOnThread state.postValue(Result.OBFUSCATED_RESOURCES)
            }

            availableResources.addAll(list.map {
                OverlaidResource(null, it.value!!, it.fullName())
            })

            if (pb != null) {
                selectedResources.addAll(pb!!.frro.typesList.flatMap { type ->
                    type.entriesList.map { entry ->
                        val name = entry.fullName(type)
                        val old = availableResources.firstOrNull { it.name == name }?.new

                        OverlaidResource.from(old, entry, name, pb!!.stringPool!!)
                    }
                })
            }

            state.postValue(Result.SUCCESS)
        }
    }

    enum class Result {
        SUCCESS, NO_RESOURCES, OBFUSCATED_RESOURCES
    }
}
