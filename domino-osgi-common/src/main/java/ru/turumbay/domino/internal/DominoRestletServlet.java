package ru.turumbay.domino.internal;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Server;
import org.restlet.engine.adapter.ServerCall;
import org.restlet.ext.servlet.ServerServlet;
import org.restlet.ext.servlet.internal.ServletCall;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DominoRestletServlet extends ServerServlet{

  private final Application app;

  public DominoRestletServlet(Application app){
    this.app = app;
  }

  @Override
  protected Application createApplication(Context parentContext) {
    return app;
  }

  @Override
  protected ServerCall createCall(Server server, HttpServletRequest request, HttpServletResponse response) {
    return new ServletCall(server, request, response){
      // getClientPort is not available in the Domino environment, so stub it with -1
      @Override
      public int getClientPort() {
        return -1;
      }
    };
  }
}