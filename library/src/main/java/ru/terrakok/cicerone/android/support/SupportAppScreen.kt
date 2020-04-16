package ru.terrakok.cicerone.android.support

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.terrakok.cicerone.Screen

/**
 * AppScreen is base class for description and creation application screen.<br></br>
 * NOTE: If you have described the creation of Intent then Activity will be started.<br></br>
 * Recommendation: Use Intents for launch external application.
 */
abstract class SupportAppScreen : Screen() {
  open val fragment: Fragment? get() = null
  open val fragmentParams: FragmentParams? get() = null

  open fun getActivityIntent(context: Context): Intent? = null
}
