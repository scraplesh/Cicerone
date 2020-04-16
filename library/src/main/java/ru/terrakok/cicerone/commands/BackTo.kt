/*
 * Created by Konstantin Tskhovrebov (aka @terrakok)
 */
package ru.terrakok.cicerone.commands

import ru.terrakok.cicerone.Screen

/**
 * Rolls fragmentBack to the needed screen from the screens chain.
 * Behavior in the case when no needed screens found depends on an implementation of the [Navigator].
 * But the recommended behavior is to return to the root.
 *
 * Creates a [BackTo] navigation command.
 *
 * @param screen screen
 */
class BackTo(val screen: Screen?) : Command
