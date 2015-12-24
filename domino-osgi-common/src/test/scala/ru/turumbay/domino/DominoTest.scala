package ru.turumbay.domino

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers._

import ru.turumbay.domino.Domino.implicits._


@RunWith(classOf[JUnitRunner])
class DominoTest extends FunSuite{

  test("should supports Lists in for-comprehension"){
    val dbs = List("names.nsf", "log.nsf")
    val groups = for{
      session <- Domino.Session
      dbName <- dbs
      db  <- session.getDatabase("",dbName)
      doc <- db.getAllDocuments
      if doc.getItemValueString("Form") == "Group"
      dt  <- doc.getLastModified
    }yield (doc.getItemValueString("ListName"))
    groups should contain ("LocalDomainServers")
  }


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
