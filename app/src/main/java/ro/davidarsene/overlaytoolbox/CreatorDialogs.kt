package ro.davidarsene.overlaytoolbox

import ro.davidarsene.overlaytoolbox.databinding.CreatorDetailsBinding
import ro.davidarsene.overlaytoolbox.trash.OverlaidResource
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.google.devrel.gmscore.tools.apk.arsc.BinaryResourceValue
import com.google.devrel.gmscore.tools.apk.arsc.TypeChunk


object CreatorDialogs {

    private val map = buildMap {
        put("bool", BooleanDialog)
        put("color", ColorDialog)
        put("dimen", DimenDialog)
        put("integer", IntegerDialog)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            put("string", StringDialog)
    }

    operator fun get(type: String) = map[type]

    val supportedTypes get() = map.keys
}

interface CreatorDialog {
    fun show(ui: CreatorDetailsBinding, res: TypeChunk.Entry, ctx: Context)

    fun create(ui: CreatorDetailsBinding, text: String, old: TypeChunk.Entry): OverlaidResource
}

interface CreatorDialogInt : CreatorDialog {
    override fun create(ui: CreatorDetailsBinding, text: String, old: TypeChunk.Entry) =
        OverlaidResource(old.value?.toString(), create(ui, text), old.fullName())

    fun create(ui: CreatorDetailsBinding, text: String): BinaryResourceValue
}

private object StringDialog : CreatorDialog {
    override fun show(ui: CreatorDetailsBinding, res: TypeChunk.Entry, ctx: Context) {
        ui.valueInput.hint = "String"
    }

    override fun create(ui: CreatorDetailsBinding, text: String, old: TypeChunk.Entry) =
        OverlaidResource(old.value?.toString(), text, old.fullName())
}

private object DimenDialog : CreatorDialogInt {

    private val units = mapOf(
        "px" to TypedValue.COMPLEX_UNIT_PX,
        "dp" to TypedValue.COMPLEX_UNIT_DIP,
        "sp" to TypedValue.COMPLEX_UNIT_SP,
        "pt" to TypedValue.COMPLEX_UNIT_PT,
        "in" to TypedValue.COMPLEX_UNIT_IN,
        "mm" to TypedValue.COMPLEX_UNIT_MM
    )

    override fun show(
        ui: CreatorDetailsBinding, res: TypeChunk.Entry, ctx: Context
    ) = with(ui.valueInput) {
        suffixText = "dp"
        hint = "Dimension"
        editText!!.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        val popup = PopupMenu(ctx, suffixTextView).apply {
            units.keys.forEach { menu.add(it) }
            setOnMenuItemClickListener {
                suffixText = it.title
                true
            }
        }
        suffixTextView.setOnClickListener { popup.show() }
    }

    override fun create(ui: CreatorDetailsBinding, text: String) = BinaryResourceValue(0,
        BinaryResourceValue.Type.DIMENSION,
        TypedValue.createComplexDimension(
            text.toFloat(),
            units[ui.valueInput.suffixText]!!
        )
    )
}

private object IntegerDialog : CreatorDialogInt {
    override fun show(ui: CreatorDetailsBinding, res: TypeChunk.Entry, ctx: Context) =
        with(ui.valueInput) {
            editText!!.inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Integer"
        }

    override fun create(ui: CreatorDetailsBinding, text: String) =
        BinaryResourceValue(0, BinaryResourceValue.Type.INT_DEC, text.toInt())
}

private object BooleanDialog : CreatorDialogInt {
    override fun show(ui: CreatorDetailsBinding, res: TypeChunk.Entry, ctx: Context) =
        with(ui.booleanValueSwitch) {
            isChecked = res.value!!.data != 0
            visibility = View.VISIBLE
            text = isChecked.toString()
            setOnCheckedChangeListener { _, isChecked ->
                text = isChecked.toString()
            }
            ui.valueInput.visibility = View.GONE
        }

    override fun create(ui: CreatorDetailsBinding, text: String) = BinaryResourceValue(0,
        BinaryResourceValue.Type.INT_BOOLEAN,
        if (ui.booleanValueSwitch.isChecked) 1 else 0
    )
}

private object ColorDialog : CreatorDialogInt {
    override fun show(ui: CreatorDetailsBinding, res: TypeChunk.Entry, ctx: Context) =
        with(ui.valueInput) {
            prefixText = "#"
            hint = "Color: (aa)rrggbb"
            editText!!.filters = arrayOf(
                InputFilter.LengthFilter(8),
                InputFilter.AllCaps(),
                InputFilter { source, _, _, _, _, _ ->
                    source.filter { Character.digit(it, 16) != -1 }
                }
            )
        }

    override fun create(ui: CreatorDetailsBinding, text: String) = BinaryResourceValue(0,
        BinaryResourceValue.Type.INT_COLOR_ARGB8,
        Color.parseColor("#$text")
    )
}
