package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

object HapticFeedbackHelper {

  fun performTapHaptic(hapticFeedback: HapticFeedback) {
    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
  }

  fun performActionHaptic(context: Context) {
    try {
      val vibrator = getVibrator(context)
      if (vibrator?.hasVibrator() == true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator.vibrate(
            VibrationEffect.createOneShot(35L, VibrationEffect.DEFAULT_AMPLITUDE)
          )
        } else {
          @Suppress("DEPRECATION")
          vibrator.vibrate(35L)
        }
      }
    } catch (_: Exception) {
    }
  }

  fun performHazardAlertHaptic(context: Context) {
    try {
      val vibrator = getVibrator(context)
      if (vibrator?.hasVibrator() == true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          // Subtle double-pulse alert for proximity to hazard zone
          val timings = longArrayOf(0, 45, 70, 45)
          val amplitudes = intArrayOf(0, 180, 0, 180)
          vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
          @Suppress("DEPRECATION")
          vibrator.vibrate(longArrayOf(0, 45, 70, 45), -1)
        }
      }
    } catch (_: Exception) {
    }
  }

  private fun getVibrator(context: Context): Vibrator? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      manager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  }
}
