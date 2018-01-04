package com.foolchen.lib.keyboardresizer

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * [Application.ActivityLifecycleCallbacks]的实现类，与[KeyboardResizerLifeCallbacks]结合使用，实现了对[Activity]生命周期的监听。
 * 供[KeyboardResizer]使用
 * @author chenchong
 * 2018/1/2
 * 下午5:16
 */

internal class ActivityLifecycleCallbacksImpl(
    private val keyboardResizerLifeCallbacks: KeyboardResizerLifeCallbacks) : Application.ActivityLifecycleCallbacks {
  override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    keyboardResizerLifeCallbacks.onCreate(activity)
  }

  override fun onActivityStarted(activity: Activity?) {
  }

  override fun onActivityResumed(activity: Activity?) {
    keyboardResizerLifeCallbacks.onResume(activity)
  }

  override fun onActivityPaused(activity: Activity?) {
    keyboardResizerLifeCallbacks.onPause(activity)
  }

  override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
  }

  override fun onActivityStopped(activity: Activity?) {
  }

  override fun onActivityDestroyed(activity: Activity?) {
    keyboardResizerLifeCallbacks.onDestroy(activity)
  }
}