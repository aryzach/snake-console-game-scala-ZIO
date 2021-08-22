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

  val moves: ZStream[Console, IOException, Direction] = inputs.collect {
    case 'd' => Direction.Right 
    case 'a' => Direction.Left
    case 's' => Direction.Down
    case 'w' => Direction.Up
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
