package ru.terrakok.cicerone

import ru.terrakok.cicerone.commands.Back
import ru.terrakok.cicerone.commands.BackTo
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace

/**
 * Router is the class for high-level navigation.
 * Use it to perform needed transitions.<br></br>
 * This implementation covers almost all cases needed for the average app.
 * Extend it if you need some tricky navigation.
 */
class Router : BaseRouter() {
  /**
   * Open new screen and add it to the screens chain.
   *
   * @param screen screen
   */
  fun navigateTo(screen: Screen) {
    executeCommands(Forward(screen))
  }

  /**
   * Clear all screens and open new one as root.
   *
   * @param screen screen
   */
  fun newRootScreen(screen: Screen) {
    executeCommands(
        BackTo(null),
        Replace(screen)
    )
  }

  /**
   * Replace current screen.
   * By replacing the screen, you alters the backstack,
   * so by going fragmentBack you will return to the previous screen
   * and not to the replaced one.
   *
   * @param screen screen
   */
  fun replaceScreen(screen: Screen) {
    executeCommands(Replace(screen))
  }

  /**
   * Return fragmentBack to the needed screen from the chain.
   * Behavior in the case when no needed screens found depends on
   * the processing of the [BackTo] command in a [Navigator] implementation.
   *
   * @param screen screen
   */
  fun backTo(screen: Screen?) {
    executeCommands(BackTo(screen))
  }

  /**
   * Opens several screens inside single transaction.
   * @param screens
   */
  fun newChain(vararg screens: Screen) {
    executeCommands(*screens.map { screen -> Forward(screen) }.toTypedArray())
  }

  /**
   * Clear current stack and open several screens inside single transaction.
   * @param screens
   */
  fun newRootChain(vararg screens: Screen) {
    val commands = mutableListOf<Command>(BackTo(null))
    if (screens.isNotEmpty()) {
      commands += Replace(screens[0])
      screens.drop(1)
          .forEach { screen -> commands += Forward(screen) }
    }
    executeCommands(*commands.toTypedArray())
  }

  /**
   * Remove all screens from the chain and exit.
   * It's mostly used to finish the application or close a supplementary navigation chain.
   */
  fun finishChain() {
    executeCommands(
        BackTo(null),
        Back()
    )
  }

  /**
   * Return to the previous screen in the chain.
   * Behavior in the case when the current screen is the root depends on
   * the processing of the [Back] command in a [Navigator] implementation.
   */
  fun exit() {
    executeCommands(Back())
  }
}
