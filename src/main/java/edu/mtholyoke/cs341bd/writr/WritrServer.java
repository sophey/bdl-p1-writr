package edu.mtholyoke.cs341bd.writr;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author jfoley
 */
public class WritrServer extends AbstractHandler {
  Server jettyServer;

  public WritrServer(int port) throws IOException {
    jettyServer = new Server(port);

    ContextHandler staticCtx = new ContextHandler();
    staticCtx.setContextPath("/static/");
    ResourceHandler resources = new ResourceHandler();
    resources.setBaseResource(Resource.newResource("static/"));
    staticCtx.setHandler(resources);

    ContextHandler defaultCtx = new ContextHandler();
    defaultCtx.setContextPath("/");
    defaultCtx.setHandler(this);

    ContextHandlerCollection collection = new ContextHandlerCollection();
    collection.addHandler(staticCtx);
    collection.addHandler(defaultCtx);

    jettyServer.setHandler(collection);
  }

  public void run() throws Exception {
    jettyServer.start();
    jettyServer.join(); // wait for it to finish here!
  }

  @Override
  public void handle(String resource, Request jettyReq, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    System.out.println(jettyReq);

    try (PrintWriter writer = resp.getWriter()) {
      writer.println("<html>");
      writer.println("<head>");
      writer.println("<title>"+resource+"</title>");
      writer.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"static/writr.css\">");
      writer.println("</head>");
      writer.println("<body>");
      writer.println("<pre>"+jettyReq.toString()+"</pre>");
      writer.println("</body>");
      writer.println("</html>");
    }
  }
}
