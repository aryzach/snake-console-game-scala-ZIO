package com.example.userinput

import cats.effect.IO
import fs2.Stream
import org.jline.terminal.{Terminal, TerminalBuilder}

/**
  * @author leopold
  * @since 26/09/18
  */
object UserInput {

  private val reader = createTerminal.reader()
  private val inputs = Stream.repeatEval(IO { reader.read() })

  val moves: Stream[IO, Char] = inputs.collect {
    case 'w' => 'w'
    case 'd' => 'd' 
    case 'a' => 'a'
    case 's' => 's' 
    case ' ' => ' '
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
