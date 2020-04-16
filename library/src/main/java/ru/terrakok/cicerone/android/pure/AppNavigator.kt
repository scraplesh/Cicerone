package ru.terrakok.cicerone.android.pure

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Back
import ru.terrakok.cicerone.commands.BackTo
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace
import java.util.*

/**
 * Navigator implementation for launch fragments and activities.<br></br>
 * Feature [BackTo] works only for fragments.<br></br>
 * Recommendation: most useful for Single-Activity application.
 */
class AppNavigator(
    protected val activity: Activity,
    protected val fragmentManager: FragmentManager,
    protected val containerId: Int
) : Navigator {
  constructor(activity: Activity, containerId: Int) : this(
      activity,
      activity.fragmentManager,
      containerId
  )

  protected var localStackCopy: LinkedList<String>? = null

  override fun applyCommands(commands: Array<out Command>) {
    fragmentManager.executePendingTransactions()

    //copy stack before apply commands
    copyStackToLocal()
    for (command in commands) {
      try {
        applyCommand(command)
      } catch (e: RuntimeException) {
        errorOnApplyCommand(command, e)
      }
    }
  }

  private fun copyStackToLocal() {
    localStackCopy = LinkedList()
    val stackSize = fragmentManager.backStackEntryCount
    for (i in 0 until stackSize) {
      localStackCopy!!.add(fragmentManager.getBackStackEntryAt(i).name)
    }
  }

  /**
   * Perform transition described by the navigation command
   *
   * @param command the navigation command to apply
   */
  protected fun applyCommand(command: Command) {
    if (command is Forward) {
      activityForward(command)
    } else if (command is Replace) {
      activityReplace(command)
    } else if (command is BackTo) {
      backTo(command)
    } else if (command is Back) {
      fragmentBack()
    }
  }

  protected fun activityForward(command: Forward) {
    val screen = command.screen as AppScreen
    val activityIntent = screen.getActivityIntent(activity)

    // Start activity
    if (activityIntent != null) {
      val options = createStartActivityOptions(command, activityIntent)
      checkAndStartActivity(screen, activityIntent, options)
    } else {
      fragmentForward(command)
    }
  }

  protected fun fragmentForward(command: Forward) {
    val screen = command.screen as AppScreen
    val fragment = createFragment(screen)
    val fragmentTransaction = fragmentManager.beginTransaction()
    setupFragmentTransaction(
        command,
        fragmentManager.findFragmentById(containerId),
        fragment,
        fragmentTransaction
    )
    fragmentTransaction
        .replace(containerId, fragment)
        .addToBackStack(screen.screenKey)
        .commit()
    localStackCopy!!.add(screen.screenKey)
  }

  protected fun fragmentBack() {
    if (localStackCopy!!.size > 0) {
      fragmentManager.popBackStack()
      localStackCopy!!.removeLast()
    } else {
      activityBack()
    }
  }

  protected fun activityBack() {
    activity.finish()
  }

  protected fun activityReplace(command: Replace) {
    val screen = command.screen as AppScreen
    val activityIntent = screen.getActivityIntent(activity)

    // Replace activity
    if (activityIntent != null) {
      val options = createStartActivityOptions(command, activityIntent)
      checkAndStartActivity(screen, activityIntent, options)
      activity.finish()
    } else {
      fragmentReplace(command)
    }
  }

  protected fun fragmentReplace(command: Replace) {
    val screen = command.screen as AppScreen
    val fragment = createFragment(screen)
    if (localStackCopy!!.size > 0) {
      fragmentManager.popBackStack()
      localStackCopy!!.removeLast()
      val fragmentTransaction = fragmentManager.beginTransaction()
      setupFragmentTransaction(
          command,
          fragmentManager.findFragmentById(containerId),
          fragment,
          fragmentTransaction
      )
      fragmentTransaction
          .replace(containerId, fragment)
          .addToBackStack(screen.screenKey)
          .commit()
      localStackCopy!!.add(screen.screenKey)
    } else {
      val fragmentTransaction = fragmentManager.beginTransaction()
      setupFragmentTransaction(
          command,
          fragmentManager.findFragmentById(containerId),
          fragment,
          fragmentTransaction
      )
      fragmentTransaction
          .replace(containerId, fragment)
          .commit()
    }
  }

  /**
   * Performs [BackTo] command transition
   */
  protected fun backTo(command: BackTo) {
    if (command.screen == null) {
      backToRoot()
    } else {
      val key = command.screen!!.screenKey
      val index = localStackCopy!!.indexOf(key)
      val size = localStackCopy!!.size
      if (index != -1) {
        for (i in 1 until size - index) {
          localStackCopy!!.removeLast()
        }
        fragmentManager.popBackStack(key, 0)
      } else {
        backToUnexisting((command.screen as AppScreen?)!!)
      }
    }
  }

  private fun backToRoot() {
    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    localStackCopy!!.clear()
  }

  /**
   * Override this method to setup fragment transaction [FragmentTransaction].
   * For example: setCustomAnimations(...), addSharedElement(...) or setReorderingAllowed(...)
   *
   * @param command             current navigation command. Will be only [Forward] or [Replace]
   * @param currentFragment     current fragment in container
   * (for [Replace] command it will be screen previous in new chain, NOT replaced screen)
   * @param nextFragment        next screen fragment
   * @param fragmentTransaction fragment transaction
   */
  protected fun setupFragmentTransaction(command: Command,
                                         currentFragment: Fragment?,
                                         nextFragment: Fragment?,
                                         fragmentTransaction: FragmentTransaction) {
  }

  /**
   * Override this method to create option for start activity
   *
   * @param command        current navigation command. Will be only [Forward] or [Replace]
   * @param activityIntent activity intent
   * @return transition options
   */
  protected fun createStartActivityOptions(command: Command, activityIntent: Intent): Bundle? {
    return null
  }

  private fun checkAndStartActivity(screen: AppScreen,
                                    activityIntent: Intent,
                                    options: Bundle?) {
    // Check if we can start activity
    if (activityIntent.resolveActivity(activity.packageManager) != null) {
      activity.startActivity(activityIntent, options)
    } else {
      unexistingActivity(screen, activityIntent)
    }
  }

  /**
   * Called when there is no activity to open `screenKey`.
   *
   * @param screen         screen
   * @param activityIntent intent passed to start Activity for the `screenKey`
   */
  protected fun unexistingActivity(screen: AppScreen, activityIntent: Intent) {
    // Do nothing by default
  }

  /**
   * Creates Fragment matching `screenKey`.
   *
   * @param screen screen
   * @return instantiated fragment for the passed screen
   */
  protected fun createFragment(screen: AppScreen): Fragment? {
    val fragment = screen.fragment
    if (fragment == null) {
      errorWhileCreatingScreen(screen)
      throw RuntimeException("Can't create a screen: " + screen.screenKey)
    }
    return fragment
  }

  /**
   * Called when we tried to fragmentBack to some specific screen (via [BackTo] command),
   * but didn't found it.
   *
   * @param screen screen
   */
  protected fun backToUnexisting(screen: AppScreen) {
    backToRoot()
  }

  /**
   * Called when we tried to create new intent or fragment but didn't receive them.
   *
   * @param screen screen
   */
  protected fun errorWhileCreatingScreen(screen: AppScreen) {
    // Do nothing by default
  }

  /**
   * Override this method if you want to handle apply command error.
   *
   * @param command command
   * @param error error
   */
  protected fun errorOnApplyCommand(
      command: Command,
      error: RuntimeException
  ) {
    throw error
  }

}
