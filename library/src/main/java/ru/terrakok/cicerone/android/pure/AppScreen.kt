package ru.terrakok.cicerone.android.pure

import android.app.Fragment
import android.content.Context
import android.content.Intent
import ru.terrakok.cicerone.Screen

/**
 * AppScreen is base class for description and creation application screen.<br></br>
 * NOTE: If you have described the creation of Intent then Activity will be started.<br></br>
 * Recommendation: Use Intents for launch external application.
 */
abstract class AppScreen : Screen() {
  val fragment: Fragment? get() = null

  fun getActivityIntent(context: Context?): Intent? = null
}
