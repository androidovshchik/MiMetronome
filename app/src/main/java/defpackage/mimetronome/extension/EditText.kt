package defpackage.mimetronome.extension

import android.widget.EditText

fun EditText.setTextSelection(text: CharSequence?) {
    setText(text)
    setSelection(getText()?.length ?: -1)
}