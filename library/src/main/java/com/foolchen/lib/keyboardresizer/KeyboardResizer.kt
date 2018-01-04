package com.foolchen.lib.keyboardresizer

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

/**
 * 系统键盘与自定义键盘（表情键盘等）的处理工具
 *
 * @author chenchong
 * 2018/1/2
 * 下午5:03
 */
class KeyboardResizer(var activity: Activity?,
    var keyboard: View?,
    var callbacks: KeyboardResizerCallBacks? = null) : KeyboardResizerLifeCallbacks, KeyboardResizerCallBacks {
  /** 键盘关闭 */
  private val KEYBOARD_STATE_CLOSED = 0
  /***/
  private val KEYBOARD_STATE_OVERLAYING = 1
  /** 键盘被系统软键盘覆盖 */
  private val KEYBOARD_STATE_OVERLAID = 2
  /** 键盘正在展示 */
  private val KEYBOARD_STATE_DISPLAY = 3

  private var content: ViewGroup? = activity?.findViewById(android.R.id.content)
  private val lifecycleCallbacksImpl: ActivityLifecycleCallbacksImpl = ActivityLifecycleCallbacksImpl(
      this)
  private val onGlobalLayoutListenerImpl = OnGlobalLayoutListenerImpl(content, this)
  private var keyboardHeight = -1

  private var keyboardState = KEYBOARD_STATE_CLOSED

  private var keyboardHideRunnable = Runnable {
    keyboard?.visibility = View.GONE
    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
  }

  init {
    // 此处注册Activity生命周期的监听
    activity?.application?.registerActivityLifecycleCallbacks(lifecycleCallbacksImpl)
  }

  override fun onResume(activity: Activity?) {
    content?.viewTreeObserver?.addOnGlobalLayoutListener(onGlobalLayoutListenerImpl)
  }

  override fun onPause(activity: Activity?) {
    keyboard?.removeCallbacks(keyboardHideRunnable)
    content?.viewTreeObserver?.removeOnGlobalLayoutListener(onGlobalLayoutListenerImpl)
  }

  override fun onDestroy(activity: Activity?) {
    activity?.application?.unregisterActivityLifecycleCallbacks(lifecycleCallbacksImpl)
    keyboard = null
    content = null
    callbacks = null
    this@KeyboardResizer.activity = null
  }

  override fun onKeyboardVisibilityChanged(isVisible: Boolean, height: Int) {
    when {
      isVisible -> keyboardHeight = height

      keyboardState == KEYBOARD_STATE_OVERLAYING -> {
        //keyboard?.visibility = View.INVISIBLE
        keyboardState = KEYBOARD_STATE_OVERLAID

        /*keyboard?.visibility = View.GONE
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)*/
        keyboard?.postDelayed(keyboardHideRunnable, 100)
      }

      keyboardState == KEYBOARD_STATE_OVERLAID -> {
        // 如果软键盘覆盖自定义键盘时，软键盘被关闭，则此时自定义键盘也关闭
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        keyboard?.visibility = View.GONE
        keyboardState = KEYBOARD_STATE_CLOSED
      }
    }
    callbacks?.onKeyboardVisibilityChanged(isVisible, height)
  }


  /**
   * 显示（自定义）键盘并关闭软键盘
   */
  fun showKeyboardWithSoftInputClose() {
    // 首先重置键盘的高度，使之与系统软键盘高度保持一致，防止抖动
    resizeKeyboard()
    // 然后，将压缩布局的方式设置为ADJUST_NOTHING，防止抖动
    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    // 打开键盘
    keyboard?.visibility = View.VISIBLE
    // 关闭软键盘
    activity?.currentFocus?.let {
      val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
    keyboardState = KEYBOARD_STATE_DISPLAY
  }

  /**
   * 关闭（自定义）键盘并打开软键盘
   */
  fun hideKeyboardWithSoftInputOpen() {
    /*// 将键盘隐藏，但是占位
    keyboard?.visibility = View.INVISIBLE*/
    // 将压缩布局方式设置为ADJUST_NOTHING，防止抖动
    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    // 打开软键盘
    activity?.currentFocus?.let {
      val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.showSoftInput(it, 0)
    }
    keyboardState = KEYBOARD_STATE_OVERLAYING
  }

  fun hideKeyboard() {
    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    keyboard?.visibility = View.GONE
    // 关闭软键盘
    activity?.currentFocus?.let {
      val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
  }

  private fun resizeKeyboard() {
    keyboard?.let {
      var layoutParams = it.layoutParams
      if (layoutParams == null) {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight)
        it.layoutParams = layoutParams
      } else if (layoutParams.height != keyboardHeight) {
        layoutParams.height = keyboardHeight
        it.layoutParams = layoutParams
      }
    }
  }
}