package com.foolchen.lib.keyboardresizer

import android.app.Activity

/**
 * [KeyboardResizer]的生命周期回调接口，用于对Activity的生命周期进行监听
 *
 * @author chenchong
 * 2018/1/2
 * 下午5:14
 */
internal interface KeyboardResizerLifeCallbacks {

  fun onCreate(activity: Activity?) {}

  fun onResume(activity: Activity?) {}

  fun onPause(activity: Activity?) {}

  fun onDestroy(activity: Activity?) {}
}