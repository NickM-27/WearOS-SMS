package com.nick.mowen.wearossms.extension

import android.app.Activity
import android.text.InputType
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import com.nick.mowen.wearossms.R

fun Activity.textInputDialog(current: String = "", hint: String = "", listener: (String) -> (Unit)) {
    val text = AppCompatEditText(this).apply {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
        setText(current)
    }

    AlertDialog.Builder(this)
            .setTitle(when {
                hint.isNotEmpty() -> hint
                current.isEmpty() -> getString(R.string.title_new_reply)
                else -> getString(R.string.title_edit_reply)
            }).setView(text)
            .setPositiveButton(getString(R.string.action_dialog_save)) { dialog, _ ->
                listener(text.text.toString())
                dialog.dismiss()
            }.setNegativeButton(getString(R.string.action_cancel), null)
            .show()
}