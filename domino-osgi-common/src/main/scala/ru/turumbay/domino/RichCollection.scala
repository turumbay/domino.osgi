package ru.turumbay.domino

import lotus.domino.{ViewEntry, Document, ViewNavigator}
import scala.annotation.tailrec
import ru.turumbay.domino.Domino.Recyclable

class RichCollection[A <: Recyclable,C <: Recyclable](xs: C, firstA: C => A, nextA: (C, A) => A,  filter: A => Boolean = (x: A) => true) {
  def map[T](f: A => T): List[T] = {
    @tailrec
    def iteration(currentEl: Option[A], acc: List[T]): List[T] = currentEl match {
      case None => acc
      case Some(el) => {
        val (newAcc: List[T], newCurrent: Option[A]) = Domino.using(el) { doc =>
          (if (filter(doc)) f(doc)::acc else acc, Option(nextA(xs, doc)))
        }
        iteration(newCurrent, newAcc)
      }
    }
    
    iteration(Option(firstA(xs)), Nil)
  }

  def flatMap[T](f: A => T) = map(f)

  def withFilter(p: A => Boolean): RichCollection[A, C] = {
    new RichCollection(xs, firstA, nextA, (x: A) => filter(x) && p(x))
  }

  def filter(p: A=>Boolean):RichCollection[A,C] = withFilter(p)
}

object RichCollection {
  type WithFirstAndNextDocument = {
    def getNextDocument(doc: Document): Document
    def getFirstDocument(): Document
    def recycle(): Unit
  }

  def apply[B <: WithFirstAndNextDocument](xs: B) = new RichCollection[Document,B](xs, _.getFirstDocument(), _.getNextDocument(_))

  def apply(nav: ViewNavigator) = new RichCollection[ViewEntry, ViewNavigator](nav, _.getFirst, _.getNext(_))
}




