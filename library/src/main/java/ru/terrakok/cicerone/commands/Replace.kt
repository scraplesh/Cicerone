/*
 * Created by Konstantin Tskhovrebov (aka @terrakok)
 */
package ru.terrakok.cicerone.commands

import ru.terrakok.cicerone.Screen

/**
 * Replaces the current screen.
 *
 * Creates a [Replace] navigation command.
 *
 * @param screen screen
 */
class Replace(val screen: Screen) : Command
