package com.vanced.manager.core.installer

import android.app.Service
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vanced.manager.ui.MainActivity

class AppUninstallerService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getIntExtra(PackageInstaller.EXTRA_STATUS, -999)) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                Log.d(AppInstallerService.TAG, "Requesting user confirmation for installation")
                val confirmationIntent = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
                confirmationIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    startActivity(confirmationIntent)
                } catch (e: Exception) {
                }
            }
            PackageInstaller.STATUS_SUCCESS -> {
                sendBroadCast(MainActivity.APP_UNINSTALLED)
                Log.d("VMpm", "Successfully uninstalled ${PackageInstaller.EXTRA_PACKAGE_NAME}")
            }
            PackageInstaller.STATUS_FAILURE -> {
                sendBroadCast(MainActivity.APP_NOT_UNINSTALLED)
                Log.d("VMpm", "Failed to uninstall ${PackageInstaller.EXTRA_PACKAGE_NAME}")
            }
        }
        stopSelf()
        return START_NOT_STICKY
    }

    private fun sendBroadCast(status: String) {
        val mIntent = Intent(status)
        mIntent.action = status
        mIntent.putExtra("pkgName", PackageInstaller.EXTRA_PACKAGE_NAME)
        LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}