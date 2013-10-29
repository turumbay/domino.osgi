package ru.turumbay.domino.internal;

import com.ibm.domino.osgi.core.context.ContextInfo;
import org.apache.felix.scr.annotations.*;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.restlet.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

@Component(specVersion = "1.1")
@References(value = {
    @Reference(
      cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
      referenceInterface = org.restlet.Application.class
    ),
    @Reference(referenceInterface = HttpService.class)
})

@SuppressWarnings("unused")
public class RestletEngine {

  HttpService httpService;

  List<Application> applications = new ArrayList<Application>();

  private final static Logger logger = LoggerFactory.getLogger(RestletEngine.class);

  protected void bindApplication(Application app) throws ServletException, NamespaceException {
    logger.info("Starting " + app.getName());
    applications.add(app);
    try {
      ContextInfo.registerServletHttpService(getBaseUrl(app), new DominoRestletServlet(app), null, null);
    } catch (NoClassDefFoundError ignore) {
      logger.info("Running outside Domino Server?");
      httpService.registerServlet(getBaseUrl(app), new DominoRestletServlet(app), null, null);
    }
  }

  protected void unbindApplication(Application app){
    logger.info("Stopping " + app.getName());
    applications.remove(app);
    try {
      ContextInfo.unregisterHttpService(getBaseUrl(app));
    } catch (NoClassDefFoundError ignore) {
      httpService.unregister(getBaseUrl(app));
    }
  }

  protected void bindHttpService(HttpService httpService) throws ServletException, NamespaceException {
    logger.debug("HttpService is here");
    if (this.httpService == null){
      this.httpService = httpService;
      for(Application app: applications){
        httpService.registerServlet(getBaseUrl(app), new DominoRestletServlet(app), null, null);
      }
    }
  }

  protected void unbindHttpService(HttpService httpService){
    logger.debug("HttpService goes out");
    for(Application app: applications){
      httpService.unregister(getBaseUrl(app));
    }
    this.httpService = null;
  }

  private String getBaseUrl(Application app){
    return "/" + app.getName();
  }
}
