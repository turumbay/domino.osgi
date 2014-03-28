package ru.turumbay.domino.internal

import org.restlet.resource.Get
import org.restlet.resource.ServerResource
import org.apache.felix.scr.annotations.{Service, Component}
import org.restlet.{Restlet, Application}
import org.restlet.routing.Router

@Component
@Service(value = Array(classOf[Application]))
class AboutApp extends Application {

  override def createInboundRoot: Restlet = {
    val router: Router = new Router(getContext)
    router.attach("/", classOf[AboutResource])
    router
  }

  override def getName: String = "restlet.example"
}


class AboutResource extends ServerResource {
  @Get def represent() ="Hello, OSGI world!"
}

