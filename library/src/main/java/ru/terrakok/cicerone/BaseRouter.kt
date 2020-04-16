/*
 * Created by Konstantin Tskhovrebov (aka @terrakok)
 */
package ru.terrakok.cicerone

import ru.terrakok.cicerone.commands.Command

/**
 * BaseRouter is an abstract class to implement high-level navigation.
 * Extend it to add needed transition methods.
 */
abstract class BaseRouter {
  val commandBuffer: CommandBuffer = CommandBuffer()

  /**
   * Sends navigation command array to [CommandBuffer].
   *
   * @param commands navigation command array to execute
   */
  protected fun executeCommands(vararg commands: Command) {
    commandBuffer.executeCommands(commands)
  }
}
