package com.foolchen.lib.keyboardresizer

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.view.WindowManager


/**
 * 检查设备是否存在虚拟导航按键
 */
internal fun Context.checkNavigationBarEnable(): Boolean {
  // 判断设备是否存在（永久性）的菜单按钮
  val hasPermanentMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
  // 判断设备是否存在返回键
  val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
  return !hasPermanentMenuKey && !hasBackKey
}

/**
 * 获取设备的虚拟导航按键高度
 */
internal fun Context.getNavigationBarHeight(): Int {
  var height = 0
  val resId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
  if (resId > 0) {
    height = resources.getDimensionPixelSize(resId)
  }
  return height
}

/**
 * 获取设备的状态栏高度
 */
internal fun Context.getStatusBarHeight(): Int {
  var height = 0
  val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
  if (resId > 0) {
    height = resources.getDimensionPixelSize(resId)
  }
  return height
}

/**
 * 使用上下文对象获取当前手机屏幕宽度
 */
internal fun Context.getScreenWidth(): Int {
  val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
  val display = wm.defaultDisplay
  val metrics = DisplayMetrics()
  display.getMetrics(metrics)
  return metrics.widthPixels
}

/**
 * 使用上下文对象获取当前手机屏幕高度
 */
internal fun Context.getScreenHeight(): Int {
  val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
  val display = wm.defaultDisplay
  val metrics = DisplayMetrics()
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
    display.getRealMetrics(metrics)
  } else {
    display.getMetrics(metrics)
  }
  return metrics.heightPixels
}

