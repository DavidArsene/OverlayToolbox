package ro.davidarsene.overlaytoolbox

import ro.davidarsene.overlaytoolbox.databinding.*
import ro.davidarsene.overlaytoolbox.model.CreatorViewModel
import ro.davidarsene.overlaytoolbox.trash.LazyAppInfo
import ro.davidarsene.overlaytoolbox.trash.ResourceAdapter
import ro.davidarsene.overlaytoolbox.util.SearchableListDialog
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


@Suppress("NewApi")
class CreatorActivity : AppCompatActivity(), View.OnClickListener {

    private val vm: CreatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ui = ActivityCreatorBinding.inflate(layoutInflater)
        setContentView(ui.root)

        vm.loadResources(intent)

        LazyAppInfo(vm.targetPackage).display(ui.target)

        vm.state.observe(this) { state ->
            when (state) {
                CreatorViewModel.Result.NO_RESOURCES -> {
                    toast(R.string.error_no_resources)
                    return@observe finish()
                }
                CreatorViewModel.Result.OBFUSCATED_RESOURCES -> {
                    toast(R.string.error_obfuscated_resources, Toast.LENGTH_LONG)
                    return@observe finish()
                }
                else -> {}
            }

            ui.valuesList.adapter = ResourceAdapter(vm.selectedResources) { res ->
                val index = vm.selectedResources.indexOf(res)
                vm.selectedResources.removeAt(index)
                ui.valuesList.adapter!!.notifyItemRemoved(index)

                if (vm.selectedResources.isEmpty()) ui.fabDone.hide()
            }

            val availableResourceAdapter = ResourceAdapter(vm.availableResources) { ores ->
                val dialog = MaterialAlertDialogBuilder(this)
                val dialogUi = CreatorDetailsBinding.inflate(layoutInflater)
                dialog.setView(dialogUi.root)

                val res = ores.actual!!.parent!!
                val dialogType = CreatorDialogs[res.typeName]!!
                dialogType.show(dialogUi, res, this)

                dialog.setPositiveButton(R.string.done) { _, _ ->
                    val text = dialogUi.valueInput.editText!!.text.toString()

                    val new = try {
                        dialogType.create(dialogUi, text, res)
                    } catch (e: Exception) {
                        return@setPositiveButton Snackbar
                            .make(ui.root, e.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    vm.selectedResources.add(new)
                    ui.valuesList.adapter!!.notifyItemInserted(vm.selectedResources.size - 1)
                    ui.fabDone.show()
                }
                dialog.setNegativeButton(android.R.string.cancel, null)
                dialog.show()
                dialogUi.valueInput.requestFocus()
            }

            ui.newValue.visibility = View.VISIBLE
            ui.valuesList.visibility = View.VISIBLE
            ui.loading.hide()

            ui.newValue.setOnClickListener {
                SearchableListDialog.show(this, availableResourceAdapter)
            }
        }

        ui.fabDone.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val ui = CreatorFinalDetailsBinding.inflate(layoutInflater)

        ui.bootloopProtection.setOnLongClickListener {
            Snackbar.make(ui.root, R.string.bootloop_protection_hint, Snackbar.LENGTH_LONG).show()
            true
        }

        ui.name.editText!!.apply {
            filters = arrayOf(
                InputFilter { source, _, _, _, _, _ ->
                    source.filter { Character.isLetterOrDigit(it) || it == '_' || it == '.' }
                }
            )
            setText(vm.name)
            addTextChangedListener { text ->
                vm.name = text.toString()
            }
        }

        MaterialAlertDialogBuilder(this).setPositiveButton(R.string.done) { _, _ ->

            val overlay = vm.createOverlay(ui.bootloopProtection.isChecked)

            startActivity(DetailsActivity::class.java) {
                putExtra(INTENT_IDENTIFIER, overlay.identifier)
            }
            finish()
        }
            .setTitle(R.string.final_details_title)
            .setView(ui.root)
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
