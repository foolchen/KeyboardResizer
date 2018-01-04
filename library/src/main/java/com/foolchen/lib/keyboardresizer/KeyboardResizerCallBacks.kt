package com.foolchen.lib.keyboardresizer

/**
 *
 * 键盘打开/关闭的回调接口
 *
 * @author chenchong
 * 2018/1/2
 * 下午5:29
 */
interface KeyboardResizerCallBacks {
  /**
   * 系统软键盘可见性改变时的回调接口
   *
   * @param isVisible 系统软键盘是否可见
   * @param height 系统软键盘高度
   */
  fun onKeyboardVisibilityChanged(isVisible: Boolean, height: Int)
}