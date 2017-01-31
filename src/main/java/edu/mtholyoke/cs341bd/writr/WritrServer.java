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
import java.util.Collections;
import java.util.Map;
import java.util.Vector;

/**
 * @author jfoley
 */
public class WritrServer extends AbstractHandler {
  String baseURL;
  String submitURL;
  Server jettyServer;
  Vector<WritrMessage> messageList = new Vector<>();

  public WritrServer(String baseURL, int port) throws IOException {
    this.baseURL = baseURL;
    if(!baseURL.endsWith("/")) {
      this.baseURL += '/';
    }
    this.submitURL = baseURL+"submit";
    jettyServer = new Server(port);

    ContextHandler staticCtx = new ContextHandler();
    staticCtx.setContextPath("/static");
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

  public String getStaticURL(String resource) {
    return baseURL+"static/"+resource;
  }

  /**
   * Made this a function so that we can have the submit form at the top & bottom of the page.
   * <a href="http://www.w3schools.com/html/html_forms.asp">Tutorial about Forms</a>
   * @param output where to write our HTML to
   */
  private void printWritrForm(PrintWriter output) {
    output.println("<div class=\"form\">");
    output.println("  <form action=\""+ submitURL +"\" method=\"POST\">");
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
    if("POST".equals(method) && submitURL.equals(path)) {
      handleForm(req, resp);
      return;
    }

    try (PrintWriter html = resp.getWriter()) {
      html.println("<html>");
      html.println("  <head>");
      html.println("    <title>Writr</title>");
      html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\""+getStaticURL("writr.css")+"\">");
      html.println("  </head>");
      html.println("  <body>");

      // Print the form at the top of the page
      printWritrForm(html);

      // Print all of our messages
      html.println("<div class=\"body\">");

      // get a copy to sort:
      ArrayList<WritrMessage> messages = new ArrayList<>(this.messageList);
      Collections.sort(messages);

      StringBuilder messageHTML = new StringBuilder();
      for (WritrMessage writrMessage : messages) {
        writrMessage.appendHTML(messageHTML);
      }
      html.println(messageHTML);
      html.println("</div>");

      // when we have a big page,
      if(messages.size() > 25) {
        // Print the submission form again at the bottom of the page
        printWritrForm(html);
      }


      html.println("  </body>");
      html.println("</html>");
    }
  }

  private void handleForm(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Map<String, String[]> parameterMap = req.getParameterMap();

    // if for some reason, we have multiple "message" fields in our form, just put a space between them, see Util.join.
    String text = Util.join(parameterMap.get("message"));

    if(text != null) {
      // Good, got new message from form.
      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      messageList.add(new WritrMessage(text));
      setupRedirectPage(resp);
      return;
    }

    // user submitted something weird.
    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad user.");
  }

  public void setupRedirectPage(HttpServletResponse resp) {
    try (PrintWriter html = resp.getWriter()) {
      html.println("<html>");
      html.println("  <head>");
      html.println("    <title>Writr: Thanks for your Submission</title>");
      html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\"static/writr.css\">");
      html.println("    "+this.baseURL);

      // Print actual redirect directive:
      html.println("<meta http-equiv=\"refresh\" content=\"0; url=front \">");

      html.println("  </head>");
      html.println("  <body>");
      html.println("    <h1>Writr: Thanks for your Submission</h1>");
      html.println("  </body>");
      html.println("</html>");
    } catch (IOException ignored) {
      // Don't consider a browser that stops listening to us after submitting a form to be an error.
    }
  }

}
