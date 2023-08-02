package org.climasense.background.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkInfo
import androidx.work.WorkQuery
import org.climasense.common.extensions.workManager

/**
 * Receiver to force app to autostart on boot
 * Does nothing, it’s just that some OEM do not respect Android policy to keep scheduled workers
 * regardless of if the app is started or not
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action.isNullOrEmpty()) return
        when (action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                /**
                 * We don’t use the return value, but querying the work manager might help bringing back
                 * scheduled workers after the app has been killed/shutdown on some devices
                 */
                context.workManager.getWorkInfosLiveData(WorkQuery.fromStates(WorkInfo.State.ENQUEUED))
            }
        }
    }
}