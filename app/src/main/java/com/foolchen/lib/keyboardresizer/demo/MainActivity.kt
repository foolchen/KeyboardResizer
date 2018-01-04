package com.foolchen.lib.keyboardresizer.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
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
    invalidateExpressionButton()
  }

  override fun onBackPressed() {
    if (mock_keyboard.visibility == View.VISIBLE) {
      keyboardResizer.hideKeyboard()
      return
    }
    super.onBackPressed()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_main_activity, menu)
    return true
  }

  override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
    val decorView = window.decorView
    menu?.findItem(R.id.immersive_mode_switch)?.isChecked = isSystemUIHidden(decorView)
    return true
  }


  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    item?.let {
      when (item.itemId) {
        R.id.immersive_mode_switch -> {
          // 切换沉浸模式
          val decorView = window.decorView
          if (isSystemUIHidden(decorView)) {
            showSystemUI(decorView)
          } else {
            hideSystemUI(decorView)
          }
          invalidateOptionsMenu()
          true
        }
        else -> false
      }
    }
    return super.onOptionsItemSelected(item)
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
  }


  // 打开表情键盘，并且关闭系统软键盘
  private fun showExpressionsWithKeyboardClose() {
    keyboardResizer.showKeyboardWithSoftInputClose()
  }

  // 关闭表情键盘，并且打开系统软键盘
  private fun closeExpressionsWithKeyboardOpen() {
    keyboardResizer.hideKeyboardWithSoftInputOpen()
  }

  // 关闭系统软键盘
  private fun closeKeyboard() {
    keyboardResizer.hideKeyboard()
  }

  private fun invalidateExpressionButton() {
    expressions.isSelected = mock_keyboard.visibility == View.VISIBLE
  }
}
