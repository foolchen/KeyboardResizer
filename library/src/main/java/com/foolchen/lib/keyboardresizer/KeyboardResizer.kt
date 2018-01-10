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
    var customKeyboard: View?,
    var callbacks: KeyboardResizerCallBacks? = null) : KeyboardResizerLifeCallbacks, KeyboardResizerCallBacks {
  /** 键盘关闭 */
  private val KEYBOARD_STATE_CLOSED = 0
  /** 键盘正在被覆盖（系统软键盘正在打开，但是尚未完全显示） */
  private val KEYBOARD_STATE_OVERLAYING = 1
  /** 键盘被系统软键盘覆盖 */
  private val KEYBOARD_STATE_OVERLAID = 2
  /** 键盘正在展示 */
  private val KEYBOARD_STATE_DISPLAY = 3

  // 从Activity中找到根布局，通过监听根布局的高度变化来计算键盘高度
  private var content: ViewGroup? = activity?.findViewById(android.R.id.content)
  private val onGlobalLayoutListenerImpl = OnGlobalLayoutListenerImpl(content, this)
  // 键盘高度，每次键盘高度变化后，该值都会改变，然后在键盘显示时重新设置键盘高度
  private var keyboardHeight = -1
  // 键盘状态
  private var keyboardState = KEYBOARD_STATE_CLOSED
  // 开发者针对Activity设置的键盘显示模式，在键盘切换完毕后都要将键盘显示模式重置回该值
  private var windowSoftInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
  private var isKeyboardVisible: Boolean? = null

  // 该Runnable用于延时将键盘显示模式重置，防止布局抖动
  private var keyboardHideRunnable = Runnable {
    customKeyboard?.visibility = View.GONE
    activity?.window?.setSoftInputMode(windowSoftInputMode)
  }

  override fun onResume(activity: Activity?) {
    this@KeyboardResizer.activity = activity
    // 获取当前Activity的键盘模式，以便于在键盘切换后能够还原到开发者设置的模式
    // 如果无法获取到开发者设定的键盘模式，则使用默认值SOFT_INPUT_ADJUST_RESIZE
    windowSoftInputMode = activity?.window?.attributes?.softInputMode ?: WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE

    // 由于在onPause()中将引用的布局和接口释放了，故此处需要重新设置
    onGlobalLayoutListenerImpl.content = content
    onGlobalLayoutListenerImpl.keyboardResizerCallBacks = this

    content?.viewTreeObserver?.addOnGlobalLayoutListener(onGlobalLayoutListenerImpl)

    if (keyboardState == KEYBOARD_STATE_DISPLAY) {
      // 如果在自定义键盘处于显示状态下，打开了系统键盘，则将自定义键盘隐藏，防止冲突
      keyboardState = KEYBOARD_STATE_CLOSED
      keyboardHideRunnable.run()
    }
  }

  override fun onPause(activity: Activity?) {
    this@KeyboardResizer.activity = null
    // 在Activity不可见时，将延时执行的Runnable移除，防止内存泄露
    customKeyboard?.removeCallbacks(keyboardHideRunnable)
    activity?.window?.setSoftInputMode(windowSoftInputMode)

    // 此处将OnGlobalLayoutListenerImpl引用的布局和接口释放，防止内存泄露
    onGlobalLayoutListenerImpl.content = null
    onGlobalLayoutListenerImpl.keyboardResizerCallBacks = null

    content?.viewTreeObserver?.removeOnGlobalLayoutListener(onGlobalLayoutListenerImpl)
  }

  override fun onDestroy(activity: Activity?) {
    this@KeyboardResizer.activity = null
    customKeyboard = null
    content = null
    callbacks = null
  }

  override fun onKeyboardVisibilityChanged(isVisible: Boolean, height: Int) {
    when {
      isVisible -> {
        keyboardHeight = height
      }

      keyboardState == KEYBOARD_STATE_OVERLAYING -> {
        keyboardState = KEYBOARD_STATE_OVERLAID
        customKeyboard?.postDelayed(keyboardHideRunnable, 300)
      }

      keyboardState == KEYBOARD_STATE_OVERLAID -> {
        // 如果软键盘覆盖自定义键盘时，软键盘被关闭，则此时自定义键盘也关闭
        //customKeyboard?.removeCallbacks(keyboardHideRunnable)
        activity?.window?.setSoftInputMode(windowSoftInputMode)
        customKeyboard?.visibility = View.GONE
        keyboardState = KEYBOARD_STATE_CLOSED
      }
    }
    if (isKeyboardVisible == null || isKeyboardVisible != isVisible) {
      isKeyboardVisible = isVisible
      callbacks?.onKeyboardVisibilityChanged(isVisible, height)
    }
  }


  /**
   * 显示（自定义）键盘并关闭软键盘
   */
  fun showCustomKeyboardWithSoftInputClose() {
    // 首先重置键盘的高度，使之与系统软键盘高度保持一致，防止抖动
    resizeKeyboard()
    // 然后，将压缩布局的方式设置为ADJUST_NOTHING，防止抖动
    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    // 打开键盘
    customKeyboard?.visibility = View.VISIBLE
    // 关闭软键盘
    hideSoftInput()
    keyboardState = KEYBOARD_STATE_DISPLAY
    customKeyboard?.removeCallbacks(keyboardHideRunnable)
  }

  /**
   * 关闭（自定义）键盘并打开软键盘
   */
  fun hideCustomKeyboardWithSoftInputOpen() {
    // 将压缩布局方式设置为ADJUST_NOTHING，防止抖动
    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    // 打开软键盘
    showSoftInput()
    keyboardState = KEYBOARD_STATE_OVERLAYING
    customKeyboard?.removeCallbacks(keyboardHideRunnable)
  }

  fun hideCustomKeyboard() {
    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    customKeyboard?.visibility = View.GONE
    keyboardState = KEYBOARD_STATE_CLOSED
  }

  fun hideSoftInput() {
    // 关闭软键盘
    activity?.currentFocus?.let {
      val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
  }

  fun showSoftInput() {
    activity?.currentFocus?.let {
      val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.showSoftInput(it, 0)
    }
  }

  private fun resizeKeyboard() {
    customKeyboard?.let {
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