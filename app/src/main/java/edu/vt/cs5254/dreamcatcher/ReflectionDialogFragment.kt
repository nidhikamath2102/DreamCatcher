package edu.vt.cs5254.dreamcatcher

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import edu.vt.cs5254.dreamcatcher.databinding.FragmentReflectionDialogBinding


class ReflectionDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val binding = FragmentReflectionDialogBinding.inflate(layoutInflater)
        var resultText = binding.reflectionText.text.toString()

        val positiveListener = DialogInterface.OnClickListener { _, _ ->
            setFragmentResult(
                REQUEST_KEY_REFLECTION,
                bundleOf(BUNDLE_KEY_REFLECTION to resultText)
            )
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle(R.string.reflection_dialog_title)
            .setPositiveButton(R.string.reflection_dialog_positive, positiveListener)
            .setNegativeButton(R.string.reflection_dialog_negative, null)
            .show()

        updatePositiveButtonState(alertDialog, resultText)
        binding.reflectionText.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                resultText = s.toString()
                updatePositiveButtonState(alertDialog, resultText)
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        return alertDialog
    }

    private fun updatePositiveButtonState(alertDialog: AlertDialog, resultText: String) {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = resultText.isNotBlank()
    }

    companion object  {
        const val REQUEST_KEY_REFLECTION = "REQUEST_KEY_REFLECTION"
        const val BUNDLE_KEY_REFLECTION = "BUNDLE_KEY_REFLECTION"
    }
}