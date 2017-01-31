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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jfoley
 */
public class WritrServer extends AbstractHandler {
  Server jettyServer;
  List<WritrMessage> messageList = new ArrayList<>();

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

  /**
   * Made this a function so that we can have the submit form at the top & bottom of the page.
   * <a href="http://www.w3schools.com/html/html_forms.asp">Tutorial about Forms</a>
   * @param output where to write our HTML to
   */
  private void printWritrForm(PrintWriter output) {
    output.println("<div class=\"form\">");
    output.println("  <form action=\"/submit\" method=\"POST\"method>");
    output.println("     <input type=\"text\" name=\"message\" />");
    output.println("     <input type=\"submit\" value=\"Write!\" />");
    output.println("  </form>");
    output.println("</div>");
  }

  @Override
  public void handle(String resource, Request jettyReq, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    System.out.println(jettyReq);

    String method = req.getMethod();
    String path = req.getPathInfo();
    if("POST".equals(method) && "/submit".equals(path)) {
      handleForm(req, resp);
      // Note, we explicitly fall through here, so that when we get a new message, we re-render the page.
    }

    try (PrintWriter html = resp.getWriter()) {
      html.println("<html>");
      html.println("  <head>");
      html.println("    <title>Writr</title>");
      html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\"static/writr.css\">");
      html.println("  </head>");
      html.println("  <body>");

      // Print the form at the top of the page
      printWritrForm(html);

      // Print all of our messages
      html.println("<div class=\"body\">");
      StringBuilder messageHTML = new StringBuilder();
      for (WritrMessage writrMessage : this.messageList) {
        writrMessage.appendHTML(messageHTML);
      }
      html.println(messageHTML);
      html.println("</div>");

      // Print the submission form again at the bottom of the page
      printWritrForm(html);


      html.println("  </body>");
      html.println("</html>");
    }
  }

  private void handleForm(HttpServletRequest req, HttpServletResponse resp) {
    Map<String, String[]> parameterMap = req.getParameterMap();

    // if for some reason, we have multiple "message" fields in our form, just put a space between them, see Util.join.
    String text = Util.join(parameterMap.get("message"));
    if(text != null) {
      messageList.add(new WritrMessage(text));
    }
  }
}
