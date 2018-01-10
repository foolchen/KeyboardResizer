package com.foolchen.lib.keyboardresizer.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.foolchen.lib.keyboardresizer.KeyboardResizer
import com.foolchen.lib.keyboardresizer.KeyboardResizerCallBacks
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), KeyboardResizerCallBacks {
  private lateinit var keyboardResizer: KeyboardResizer

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    keyboardResizer = KeyboardResizer(this, mock_keyboard, this)
    listeners()
  }

  override fun onResume() {
    super.onResume()
    keyboardResizer.onResume(this)
    invalidateExpressionButton()
  }

  override fun onPause() {
    super.onPause()
    keyboardResizer.onPause(this)
  }

  override fun onBackPressed() {
    if (mock_keyboard.visibility == View.VISIBLE) {
      keyboardResizer.hideCustomKeyboard()
      return
    }
    super.onBackPressed()
  }

  override fun onKeyboardVisibilityChanged(isVisible: Boolean, height: Int) {
    keyboard.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
  }

  private fun listeners() {
    expressions.setOnClickListener {
      if (mock_keyboard.visibility == View.VISIBLE) {
        //mock_keyboard.visibility = View.GONE
        closeExpressionsWithKeyboardOpen()
      } else {
        //mock_keyboard.visibility = View.VISIBLE
        showExpressionsWithKeyboardClose()
      }
      invalidateExpressionButton()
    }

    keyboard.setOnClickListener {
      closeKeyboard()
      invalidateExpressionButton()
    }

    et.setOnClickListener {
      if (mock_keyboard.visibility == View.VISIBLE) {
        closeExpressionsWithKeyboardOpen()
      } else {
        keyboardResizer.showSoftInput()
      }
      invalidateExpressionButton()
    }

    et.setOnFocusChangeListener { _, hasFocus ->
      if (hasFocus) {
        closeExpressionsWithKeyboardOpen()
        invalidateExpressionButton()
      }
    }
  }


  // 打开表情键盘，并且关闭系统软键盘
  private fun showExpressionsWithKeyboardClose() {
    keyboardResizer.showCustomKeyboardWithSoftInputClose()
  }

  // 关闭表情键盘，并且打开系统软键盘
  private fun closeExpressionsWithKeyboardOpen() {
    if (mock_keyboard.visibility == View.VISIBLE) {
      keyboardResizer.hideCustomKeyboardWithSoftInputOpen()
    } else {
      keyboardResizer.showSoftInput()
    }
  }

  // 关闭系统软键盘
  private fun closeKeyboard() {
    keyboardResizer.hideSoftInput()
  }

  private fun invalidateExpressionButton() {
    expressions.isSelected = mock_keyboard.visibility == View.VISIBLE
  }
}
