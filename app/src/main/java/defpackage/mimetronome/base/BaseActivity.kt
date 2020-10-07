package defpackage.mimetronome.base

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.design.longSnackbar
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseActivity : AppCompatActivity(), CoroutineScope {

    protected val job = SupervisorJob()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected open fun showMessage(message: CharSequence) {
        contentView?.longSnackbar(message)
    }

    protected open fun showError(e: Throwable) {
        showMessage(e.message ?: e.toString())
    }

    override fun onDestroy() {
        job.cancelChildren()
        super.onDestroy()
    }

    override val coroutineContext = Dispatchers.Main + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
        if (e !is CancellationException && !isFinishing) {
            runOnUiThread {
                showError(e)
            }
        }
    }
}