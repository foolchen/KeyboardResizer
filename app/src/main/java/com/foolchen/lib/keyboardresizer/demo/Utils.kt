package com.foolchen.lib.keyboardresizer.demo

import android.os.Build
import android.view.View


// This snippet hides the system bars.
fun hideSystemUI(decorView: View) {
  // Set the IMMERSIVE flag.
  // Set the content to appear under the system bars so that the content
  // doesn't resize when the system bars hide and show.
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
        or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
        or View.SYSTEM_UI_FLAG_IMMERSIVE)
  } else {
    decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
        or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
        /*or View.SYSTEM_UI_FLAG_IMMERSIVE*/)
  }
}

// This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
fun showSystemUI(decorView: View) {
  decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
      or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}

fun isSystemUIHidden(decorView: View) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
  decorView.systemUiVisibility == (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
      or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
      or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
      or View.SYSTEM_UI_FLAG_IMMERSIVE)
} else {
  decorView.systemUiVisibility == (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
      or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
      or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
      /*or View.SYSTEM_UI_FLAG_IMMERSIVE*/)
}
