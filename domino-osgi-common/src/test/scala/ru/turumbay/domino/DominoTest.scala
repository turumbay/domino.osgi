package ru.turumbay.domino

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._

import Domino.implicits._
import ru.turumbay.domino.Domino.Session

@RunWith(classOf[JUnitRunner])
class DominoTest extends FunSuite{

  test("domino objects could be used in for comprehension and it should compiles"){
    val groups = for{
      session <- Domino.Session
      db  <- session.getDatabase("","names.nsf")
      doc <- db.getAllDocuments
      if doc.getItemValueString("Form") == "Group"
      dt  <- doc.getLastModified
    }yield (doc.getItemValueString("ListName"))
    groups should contain ("LocalDomainServers")
  }

}
