package ro.davidarsene.overlaytoolbox.model

import android.app.Application
import android.content.om.OverlayInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ro.davidarsene.overlaytoolbox.*
import ro.davidarsene.overlaytoolbox.trash.OverlaidResource
import ro.davidarsene.overlaytoolbox.util.Parsers
import java.io.File

@Suppress("NewApi")
class DetailsViewModel(val app: Application) : AndroidViewModel(app) {

    val overlaidResources = MutableLiveData<List<OverlaidResource>>()

    fun loadResources(overlay: OverlayInfo) = runOnThread {
        val availableResources = Parsers.arsc(overlay.targetPackageName)
            ?.filter { it.configuration.isDefault }

        overlaidResources.postValue(if (overlay.isFabricated) {

            // Search for the original values since FRROs don't include resource IDs.
            // This is normally done by idmap2 when the overlay is created.
            val src = File(overlay.baseCodePath).inputStream()
            val proto = Parsers.proto(src)

            proto.frro.typesList.flatMap { type ->
                val oldType = availableResources?.firstOrNull { it.typeName == type.name }?.entries?.values
                type.entriesList.map { entry ->
                    val old = oldType?.find { it.key == entry.name }?.valueToString()

                    OverlaidResource.from(old, entry, entry.fullName(type), proto.stringPool!!)
                }
            }
        } else Parsers.arscFromApk(overlay.baseCodePath).flatMap { type ->
            val oldType = availableResources?.firstOrNull { it.typeName == type.typeName }?.entries?.values
            type.entries.values.map { entry ->
                val old = oldType?.find { it.key == entry.key }?.valueToString()

                OverlaidResource(old, entry.valueToString(), "$type/${entry.key}")
            }
        })
    }
}
