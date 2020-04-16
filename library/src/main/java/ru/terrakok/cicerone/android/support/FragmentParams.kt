package ru.terrakok.cicerone.android.support

import android.os.Bundle
import androidx.fragment.app.Fragment

class FragmentParams(val fragmentClass: Class<out Fragment?>, val arguments: Bundle?) {
  constructor(fragmentClass: Class<out Fragment?>) : this(fragmentClass, null)
}
