package com.foolchen.lib.keyboardresizer

import android.graphics.Rect
import android.view.ViewGroup
import android.view.ViewTreeObserver

// 键盘开关的阈值。在布局高度小于该值时，认为是无效变换
internal const val KEYBOARD_TOGGLE_THRESHOLD = 150

/**
 * [ViewTreeObserver.OnGlobalLayoutListener]的实现类，通过布局高度的改变来计算系统软键盘的高度
 *
 * 与[KeyboardResizerCallBacks]结合使用，实现软键盘开关的回调
 *
 * @author chenchong
 * 2018/1/2
 * 下午5:28
 */
class OnGlobalLayoutListenerImpl(var content: ViewGroup?,
    var keyboardResizerCallBacks: KeyboardResizerCallBacks?) : ViewTreeObserver.OnGlobalLayoutListener {
  ///////////////////////////////////////////////////////////////////////////
  // 此处content和keyboardResizerCallbacks在必要时会被释放，防止发生内存泄露
  // 故此处这两个值是可空的，在使用时要注意
  ///////////////////////////////////////////////////////////////////////////

  private val mRect = Rect()

  override fun onGlobalLayout() {
    content?.let {
      it.getWindowVisibleDisplayFrame(mRect)
      val bottom = mRect.bottom

      val context = it.context
//      Log.v("KeyboardResizer",
//          "top = $top , bottom = $bottom , height = $height , screenHeight = ${context.getScreenHeight()} " +
//              "\nisNavigationBarEnable = ${context.checkNavigationBarEnable()} , navigationBarHeight = ${context.getNavigationBarHeight()}" +
//              "\nisStatusBarEnable = ${context.getStatusBarHeight() > 0} , statusBarHeight = ${context.getStatusBarHeight()}")
      val navigationBarHeight = if (context.checkNavigationBarEnable()) context.getNavigationBarHeight() else 0
      val keyboardHeight = context.getScreenHeight() - bottom - navigationBarHeight
      keyboardResizerCallBacks?.onKeyboardVisibilityChanged(
          keyboardHeight > KEYBOARD_TOGGLE_THRESHOLD, keyboardHeight)
    }
  }
}