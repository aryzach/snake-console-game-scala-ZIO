package com.example.draw

import cats.instances.all._
import cats.syntax.all._
//import cats.syntax.traverse._
import cats.data.State
import cats.effect.IO
import com.example.draw.Draw.DrawCommand
import org.fusesource.jansi.{Ansi, AnsiConsole}

import com.example.game._
import com.example.tools._

/**
 * Based on https://github.com/m50d/console-game/blob/master/src/main/scala/example/Draw.scala
 *
 * @author leopold
 * @since 26/09/18
 */
object Draw {

  type DrawCommand = State[Ansi, Unit]

  private def ansi(modify: Ansi => Ansi): DrawCommand = State.modify[Ansi](modify)

  def eraseScreen: DrawCommand = ansi(_.eraseScreen())

  def goto(x: Int, y: Int): DrawCommand = ansi(_.cursor(x, y))
  def print(s: String): DrawCommand = ansi(_.a(s))

  def printAt(x: Int, y: Int, s: String): DrawCommand = goto(x, y) *> print(s)

  def printLinesAt(x: Int, y: Int, lines: Vector[String]): DrawCommand = {
    lines.zipWithIndex
      .traverse { case (line, idx) => printAt(x + idx, y, line) }
      .map(_ => Unit)
  }


}

object Console {
  def render(state: GameState): IO[Unit] = {
    val parts = state.parts
    val w = 30 
    val h = 30
    val s = createBoard(h, w, parts).map(showRow(_))
    draw(
      Draw.eraseScreen *>
      Draw.printAt(0,0,"_______________________________________________________________") *>
      Draw.printLinesAt(2,2,s) *>
      Draw.printAt(h+3,0,"_______________________________________________________________") *>
      Draw.printAt(h+10,0, state.event.stringify) *>
      Draw.goto(1,0)
    )
  }


  def draw(a: DrawCommand): IO[Unit] = IO { AnsiConsole.out.println(a.run(Ansi.ansi()).value._1) }
  private def showRow(row: Seq[Boolean]): String = row.map(if (_) '\u25A0' else ". ").mkString

  private def createBoard(h: Int, w: Int, p: Parts): Vector[Seq[Boolean]] = 
    (for {
      x <- (0 to w)
      } yield for {
        y <- (0 to h)
      } yield if (p.contains(Position(x,y))) true else false).toVector

}


