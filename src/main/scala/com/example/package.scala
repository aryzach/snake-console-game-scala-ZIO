package com

package object example {
  sealed abstract case class Name private (name: String)
  object Name {
    def make(name: String): Option[Name] =    if (!name.isEmpty) Some(new Name(name) {}) else None
  }

  sealed abstract case class Word private (word: String) {
    def contains(char: Char) = word.contains(char)
    val length: Int          = word.length
    def toList: List[Char]   = word.toList
    def toSet: Set[Char]     = word.toSet
  }
  object Word {
    def make(word: String): Option[Word] =
      if (!word.isEmpty && word.forall(_.isLetter)) Some(new Word(word.toLowerCase) {})
      else None
  }

  sealed abstract case class Guess private (char: Char) 
  object Guess {
    def make(guess: String): Option[Guess] = 
      Some(guess.toList).collect {
        case c :: Nil if c.isLetter => new Guess(c.toLower) {}
      }
  }


  sealed abstract case class State private (name: Name, guesses: Set[Guess], word: Word) {
    def failuresCount: Int            = (guesses.map(_.char) -- word.toSet).size
    def playerLost: Boolean           = failuresCount > 5
    def playerWon: Boolean            = (word.toSet -- guesses.map(_.char)).isEmpty
    def addGuess(guess: Guess): State = new State(name, guesses + guess, word) {}
  }
  object State {
    def initial(name: Name, word: Word): State = new State(name, Set.empty, word) {}
  }

  sealed trait GuessResult
  object GuessResult {
    case object Won       extends GuessResult
    case object Lost      extends GuessResult
    case object Correct   extends GuessResult
    case object Incorrect extends GuessResult
    case object Unchanged extends GuessResult
  }

  val words = List(
    "aaron",
    "abelian",
    "ability",
    "about",
    "abstract",
    "abstract",
    "abstraction",
    "accurately",
    "adamek",
    "add",
    "adjacent",
    "adjoint"
  )

  val hangmanStages = List(
    """
  #   --------
  #   |      |
  #   |      
  #   |    
  #   |      
  #   |     
  #   -
  #""".stripMargin('#'),
  """
  #   --------
  #   |      |
  #   |      0
  #   |    
  #   |      
  #   |     
  #   -
  #""".stripMargin('#'),
  """
  #   --------
  #   |      |
  #   |      0
  #   |      |
  #   |      |
  #   |     
  #   -
  #""".stripMargin('#'),
  """
  #   --------
  #   |      |
  #   |      0
  #   |     \|
  #   |      |
  #   |     
  #   -
  #""".stripMargin('#'),
  """
  #   --------
  #   |      |
  #   |      0
  #   |     \|/
  #   |      |
  #   |     
  #   -
  #""".stripMargin('#'),
  """
  #   --------
  #   |      |
  #   |      0
  #   |     \|/
  #   |      |
  #   |     /
  #   -
  #""".stripMargin('#'),
  """
  #   --------
  #   |      |
  #   |      0
  #   |     \|/
  #   |      |
  #   |     / \
  #   -
  #""".stripMargin('#')
)
}
