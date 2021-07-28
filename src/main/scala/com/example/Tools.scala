package com.example.tools


object Tools {
  def repeatN(x: Any, n: Int): Seq[Any] =
    n match {
      case 0 => Seq()
      case n => Seq(x) ++ repeatN(x, n - 1)
    }
}

