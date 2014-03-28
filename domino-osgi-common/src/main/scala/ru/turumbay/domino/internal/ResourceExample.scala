package ru.turumbay.domino.internal

import ru.turumbay.domino.ResourceEngine
import org.apache.felix.scr.annotations._
import org.osgi.service.http.HttpService

@Component
@References(Array(
  new Reference(referenceInterface = classOf[HttpService]))
)
class ResourceExample extends ResourceEngine{

  protected def getResourceAlias = "/resource.example"

  protected def getResourceName = "/site"
}
