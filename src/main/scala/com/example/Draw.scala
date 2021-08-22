package com.example.draw

import com.example.draw.Draw.DrawCommand
import org.fusesource.jansi.{Ansi, AnsiConsole}

import zio._
import zio.prelude._
import zio.console._

import com.example.game._
import com.example.tools._

/**
 * Based on https://github.com/m50d/console-game/blob/master/src/main/scala/example/Draw.scala
 *
 * @author leopold
 * @since 26/09/18
 */
object Draw {
/*
  trait Ansi {
    def a(s: String): Ansi
    def cursor(x: Int, y: Int): Ansi
    def eraseScreen: Ansi
    def print(s: String): Ansi
  }
*/
  type DrawCommand = State[Ansi, Unit]

  private def ansi(modify: Ansi => Ansi): DrawCommand =
    State.update(modify)

  def eraseScreen: DrawCommand = ansi(_.eraseScreen)

  def goto(x: Int, y: Int): DrawCommand = ansi(_.cursor(x, y))
  def print(s: String): DrawCommand     = ansi(_.a(s))

  def printAt(x: Int, y: Int, s: String): DrawCommand = goto(x, y).flatMap(_ => print(s))
  /* 
  def printLinesAt(x: Int, y: Int, lines: Vector[String]): DrawCommand =
    for {
      _ <- lines.zipWithIndex.forEach { case (line, idx) => printAt(x + idx, y, line) }
    } yield ()
  */
  def printLinesAt(x: Int, y: Int, lines: Vector[String]): DrawCommand =
    lines.zipWithIndex.forEach { case (line, idx) => printAt(x + idx, y, line) } map (_ => ())
  

}

object Screen {
  def render(state: GameState): ZIO[Console, Nothing, Unit] = {
    val parts = state.parts
    val w = 30 
    val h = 30
    val s = createBoard(h, w, parts).map(showRow(_))
    draw(
      for {
        _ <- Draw.eraseScreen 
        _ <- Draw.printAt(0,0,"_______________________________________________________________") 
        _ <- Draw.printLinesAt(2,2,s) 
        _ <- Draw.printAt(h+3,0,"_______________________________________________________________") 
        _ <- Draw.printAt(h+10,0, state.event.stringify) 
        _ <- Draw.goto(1,0)
      } yield ()
      )
  }


  def draw(a: DrawCommand): ZIO[Console, Nothing, Unit] = ZIO.succeed( AnsiConsole.out.println(a.run(Ansi.ansi())._1) )

  private def showRow(row: Seq[Boolean]): String = row.map(if (_) ('\u25A0' + " ") else "  ").mkString

  private def createBoard(h: Int, w: Int, p: Parts): Vector[Seq[Boolean]] = 
    (for {
      x <- (0 to w)
      } yield for {
        y <- (0 to h)
      } yield if (p.contains(Position(x,y))) true else false).toVector

}


