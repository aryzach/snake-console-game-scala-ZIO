package com.example.userinput

import cats.effect.IO
import fs2.Stream
import org.jline.terminal.{Terminal, TerminalBuilder}
import com.example.game._

import zio.stream.ZStream
//import zio._
import zio.console._
import java.io.IOException


/**
 * @author leopold
 * @since 26/09/18
 */
object UserInput {

  private val reader = createTerminal.reader()
  //  private val inputs = Stream.repeatEval(IO { reader.read() })
  private val inputs = ZStream.repeatEffect(zio.console.getStrLn)

  val moves: ZStream[Console, IOException, Direction] = inputs.collect {
    case "d" => Direction.Right 
    case "a" => Direction.Left
    case "s" => Direction.Down
    case "w" => Direction.Up
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
