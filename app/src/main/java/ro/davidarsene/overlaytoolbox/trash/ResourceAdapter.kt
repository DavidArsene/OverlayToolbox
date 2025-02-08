package ro.davidarsene.overlaytoolbox.trash

import android.graphics.Color
import android.idmap2.pb.FabricatedV1
import android.view.View
import com.google.devrel.gmscore.tools.apk.arsc.*
import com.l4digital.fastscroll.FastScroller
import ro.davidarsene.overlaytoolbox.databinding.ItemResourceBinding
import ro.davidarsene.overlaytoolbox.fullName
import ro.davidarsene.overlaytoolbox.valueToString


class ResourceAdapter(
    valueList: List<OverlaidResource>? = null,
    onItemClicked: (OverlaidResource) -> Unit,
) :
    BaseAdapter<OverlaidResource, ItemResourceBinding>
        (valueList, ItemResourceBinding::inflate, onItemClicked), FastScroller.SectionIndexer {

    @Suppress("SetTextI18n")
    override fun onBindViewHolder(item: OverlaidResource, ui: ItemResourceBinding) {
        ui.name.text = item.name

        val new = item.new
        if (item.old != null) {
            ui.value.text = "${item.old} → $new"
        } else ui.value.text = new

        try {
            if (new.startsWith('#')) {
                ui.color.setBackgroundColor(Color.parseColor(new))
                ui.color.visibility = View.VISIBLE
                return
            }
        } catch (_: Exception) { }
        ui.color.visibility = View.GONE
    }

    override fun filter(item: OverlaidResource, constraint: CharSequence) =
        item.name.contains(constraint, true) ||
                item.new.contains(constraint, true)

    override fun getSectionText(position: Int) = getItem(position).name.substringBefore('/')
}

class OverlaidResource(
    val old: String?,
    val new: String,
    val name: String,
    val actual: BinaryResourceValue? = null
) {

    companion object {
        fun from(
            old: String?,
            new: FabricatedV1.ResourceEntry,
            name: String,
            pool: StringPoolChunk
        ): OverlaidResource {
            val type = BinaryResourceValue.Type[new.resValue.dataType.toByte()]
            val data = new.resValue.dataValue
            return if (type == BinaryResourceValue.Type.STRING) {
                OverlaidResource(old, pool.getString(data), name)
            } else {
                OverlaidResource(old, BinaryResourceValue(0, type, data), name)
            }
        }
    }

    constructor(
        old: String?, new: BinaryResourceValue, name: String
    ) : this(old, new.toString(), name, new)

    constructor(
        old: String?, new: TypeChunk.Entry
    ) : this(old, new.valueToString(), new.fullName(), new.value)
}
