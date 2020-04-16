/*
 * Created by Konstantin Tskhovrebov (aka @terrakok)
 */
package ru.terrakok.cicerone.commands

import ru.terrakok.cicerone.Screen

/**
 * Opens new screen.
 *
 * Creates a [Forward] navigation command.
 *
 * @param screen screen
 */
class Forward(val screen: Screen) : Command
