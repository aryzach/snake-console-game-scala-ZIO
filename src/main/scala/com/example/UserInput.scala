package com.example.userinput

import com.example.game._

import zio.stream.ZStream
import zio.console._
import java.io.IOException
import zio._

import org.jline.terminal.{Terminal, TerminalBuilder}

object UserInput {

  private val reader = createTerminal.reader()
  private val inputs: ZStream[Console, IOException, Int] = ZStream.repeatEffect(ZIO.succeed(reader.read()))

  val moves: ZStream[Console, IOException, Position] = {
    val p = Position(0,0)
    inputs.collect {
      case 'd' => p.right
      case 'a' => p.left
      case 's' => p.down
      case 'w' => p.up
    }
  }

  private def createTerminal: Terminal = {
    val t = TerminalBuilder.builder()
      .jansi(true)
      .system(true)
      .build()
      t.enterRawMode()
      t
  }

}
