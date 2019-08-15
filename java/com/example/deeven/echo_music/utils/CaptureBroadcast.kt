package com.example.deeven.echo_music.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.deeven.echo_music.R
import com.example.deeven.echo_music.activities.MainActivity
import com.example.deeven.echo_music.fragments.NowPlayingFragment

class CaptureBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try {
                MainActivity.Statified.notificationManager?.cancel(1978)
            }catch (e: Exception){
                e.printStackTrace()
            }
            if (NowPlayingFragment.Statified.mediaPlayer?.isPlaying == true) {
                try {
                    NowPlayingFragment.Statified.mediaPlayer?.pause()
                    NowPlayingFragment.Statified.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            val tm: TelephonyManager = context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when (tm.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    try {
                        MainActivity.Statified.notificationManager?.cancel(1978)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    try {
                        NowPlayingFragment.Statified.mediaPlayer?.pause()
                        NowPlayingFragment.Statified.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else -> {

                }
            }
        }
    }
}