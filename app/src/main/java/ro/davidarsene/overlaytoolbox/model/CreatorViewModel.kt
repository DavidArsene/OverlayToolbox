package ro.davidarsene.overlaytoolbox.model

import android.app.Application
import android.content.Intent
import android.content.om.FabricatedOverlay
import android.content.om.OverlayManagerTransaction
import android.os.FabricatedOverlayInternalEntry
import android.util.TypedValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ro.davidarsene.overlaytoolbox.*
import ro.davidarsene.overlaytoolbox.trash.OverlaidResource
import ro.davidarsene.overlaytoolbox.util.Parsers
import ro.davidarsene.overlaytoolbox.util.RootHelper

class CreatorViewModel(val app: Application) : AndroidViewModel(app) {

    val availableResources = ArrayList<OverlaidResource>()
    val selectedResources = ArrayList<OverlaidResource>()
    lateinit var targetPackage: String
    val state = MutableLiveData<InitResult>()
    var name = ""

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

        @Suppress("Deprecation", "UNCHECKED_CAST") // Use old methods instead of A14 ones to support A12+
        selectedResources.forEach { res ->
            if (res.actual == null) {
                overlay.setResourceValue(res.name, TypedValue.TYPE_STRING, res.new)
            } else {
                // overlay.setResourceValue(res.name, res.actual.type.code.toInt(), res.actual.data)
                // Bypass wrong range check in framework, TYPE_DIMENSION is not between TYPE_FIRST_INT and TYPE_LAST_INT
                val entry = FabricatedOverlayInternalEntry().apply {
                    resourceName = res.name
                    dataType = res.actual.type.code.toInt() // without checkArgumentInRange
                    data = res.actual.data
                }
                // overlay.mEntries.add(entry)
                // noinspection BlockedPrivateApi
                overlay.javaClass.getDeclaredField("mEntries").apply {
                    isAccessible = true
                    val entries = get(overlay) as ArrayList<FabricatedOverlayInternalEntry>
                    entries.add(entry)
                }
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
            name = pb.name
            pb.frro.name
        }

        runOnThread {
            val materialFilter = app.resources.getStringArray(R.array.mdc_resources)
            val types = Parsers.arsc(targetPackage)!!
            val list = if (!app.resources.getBoolean(R.bool.settings_show_all_types))

                CreatorDialogs.supportedTypes.flatMap { type ->
                    val list = (types
                        .firstOrNull { it.typeName == type && it.configuration.isDefault }
                        ?: return@flatMap emptyList())
                        .entries.values.toList()

                    if (app.resources.getBoolean(R.bool.settings_hide_mdc_lib)) {
                        list.filter { materialFilter.none { str -> it.key.contains(str) } }
                    } else list
                }
            else types.filter { it.configuration.isDefault }.flatMap { type ->
                type.entries.values.toList()
            }

            if (list.isEmpty()) {
                return@runOnThread state.postValue(InitResult.NO_RESOURCES)
            }
            // Fabricated Overlays are based on resource names (not IDs),
            // so any app with duplicated resource names is not supported
            if (list[0].key == list[1].key) {
                return@runOnThread state.postValue(InitResult.OBFUSCATED_RESOURCES)
            }

            availableResources.addAll(list.map { OverlaidResource(null, it) })

            if (pb != null) {
                selectedResources.addAll(pb.frro.typesList.flatMap { type ->
                    type.entriesList.map { entry ->
                        val name = entry.fullName(type)
                        val old = availableResources.firstOrNull { it.name == name }?.new

                        OverlaidResource.from(old, entry, name, pb.stringPool!!)
                    }
                })
            }

            state.postValue(InitResult.SUCCESS)
        }
    }

    enum class InitResult { SUCCESS, NO_RESOURCES, OBFUSCATED_RESOURCES }
}
