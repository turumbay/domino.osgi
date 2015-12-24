package ru.turumbay.domino

import lotus.domino._
import com.ibm.domino.osgi.core.context.ContextInfo

case class Domino[A <: Domino.Recyclable ](value:A){
  def map[B](f: A => B) = Domino.using(value)(f)
  def flatMap[B](f: A => B) = map(f)
}

object Domino extends Logging{
  type Recyclable = {def recycle()}

  object implicits{
    implicit def toDomino[A <: Recyclable](a:A) = Domino(a)
    implicit def toRichCollection(collection:DocumentCollection) = RichCollection(collection)
    implicit def toRichCollection(view:View) = RichCollection(view)
    implicit def toRichCollection(nav:ViewNavigator) = RichCollection(nav)

    implicit def toRichDocument(doc:Document) = RichDocument(doc)
  }

  object Session{
    def flatMap[T](f: Session => T) = Domino.withSession(f)
  }

  def withSession[T](f: Session => T): T =
    dominoOsgiCoreContextSession match {
      case Some(session) => f(session)
      case None => withLocalSession(f)
    }

  def using[A, B <: Recyclable](recyclable: B)(f: B => A): A =
    try{
      f(recyclable)
    }finally{
      try recyclable.recycle() catch { case _:NotesException => }
    }

  def dominoOsgiCoreContextSession: Option[Session] =
    try{
      Option(ContextInfo.getUserSession)

    }catch{
      case _:NoClassDefFoundError => None
    }

  def withLocalSession[T](f: Session => T): T =
    try {
      NotesThread.sinitThread()
      using(NotesFactory.createSession)(f)
    } finally{
      NotesThread.stermThread()
    }
}



