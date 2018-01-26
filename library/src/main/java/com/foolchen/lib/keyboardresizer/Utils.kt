package com.foolchen.lib.keyboardresizer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
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

  if (isHuaWei()) {
    return (!hasPermanentMenuKey && !hasBackKey || checkHuaWeiDeviceHasNavigationBar(
        this)) && isNavigationBarShow(this)
  } else {
    return !hasPermanentMenuKey && !hasBackKey && isNavigationBarShow(this)
  }
}

@SuppressLint("PrivateApi")
//获取是否存在NavigationBar
fun checkHuaWeiDeviceHasNavigationBar(context: Context): Boolean {
  var hasNavigationBar = false
  try {
    val rs = context.resources
    val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
    if (id > 0) {
      hasNavigationBar = rs.getBoolean(id)
    }
    val systemPropertiesClass = Class.forName("android.os.SystemProperties")
    val m = systemPropertiesClass.getMethod("get", String::class.java)
    val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
    if ("1" == navBarOverride) {
      hasNavigationBar = false
    } else if ("0" == navBarOverride) {
      hasNavigationBar = true
    }
  } catch (e: Exception) {

  }

  return hasNavigationBar
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

/**
 * 判断是否需是华为设备
 */
private fun isHuaWei() = Build.BRAND.contains("Huawei", true)

//NavigationBar状态是否是显示
private fun isNavigationBarShow(context: Context): Boolean {

  val activity: Activity? = if (context is ContextWrapper) {
    context.baseContext as? Activity?
  } else {
    context as? Activity?
  }
  if (activity != null) {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      val display = activity.windowManager.defaultDisplay
      val size = Point()
      val realSize = Point()
      display.getSize(size)
      display.getRealSize(realSize)
      realSize.y != size.y
    } else {
      val menu = ViewConfiguration.get(context).hasPermanentMenuKey()
      val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
      !(menu || back)
    }
  } else {
    return false
  }
}
