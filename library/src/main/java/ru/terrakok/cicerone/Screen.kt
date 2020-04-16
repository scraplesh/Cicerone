package ru.terrakok.cicerone

/**
 * Screen is class for description application screen.
 */
abstract class Screen {
  var screenKey: String = javaClass.canonicalName
    protected set
}
