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
  String metaURL;
  Server jettyServer;
  Vector<WritrMessage> messageList = new Vector<>();

  public WritrServer(String baseURL, int port) throws IOException {
    this.metaURL = "<base href=\""+baseURL+"\">";
    jettyServer = new Server(port);

    // We create a ContextHandler, since it will catch requests for us under a specific path.
    // This is so that we can delegate to Jetty's default ResourceHandler to serve static files, e.g. CSS & images.
    ContextHandler staticCtx = new ContextHandler();
    staticCtx.setContextPath("/static");
    ResourceHandler resources = new ResourceHandler();
    resources.setBaseResource(Resource.newResource("static/"));
    staticCtx.setHandler(resources);

    // This context handler just points to the "handle" method of this class.
    ContextHandler defaultCtx = new ContextHandler();
    defaultCtx.setContextPath("/");
    defaultCtx.setHandler(this);

    // Tell Jetty to use these handlers in the following order:
    ContextHandlerCollection collection = new ContextHandlerCollection();
    collection.addHandler(staticCtx);
    collection.addHandler(defaultCtx);
    jettyServer.setHandler(collection);
  }

  /**
   * Once everything is set up in the constructor, actually start the server here:
   * @throws Exception if something goes wrong.
   */
  public void run() throws Exception {
    jettyServer.start();
    jettyServer.join(); // wait for it to finish here! We're using threads behind the scenes; so this keeps the main thread around until something can happen!
  }

  public String getStaticURL(String resource) {
    return "static/"+resource;
  }

  /**
   * Made this a function so that we can have the submit form at the top & bottom of the page.
   * <a href="http://www.w3schools.com/html/html_forms.asp">Tutorial about Forms</a>
   * @param output where to write our HTML to
   */
  private void printWritrForm(PrintWriter output) {
    output.println("<div class=\"form\">");
    output.println("  <form action=\"submit\" method=\"POST\">");
    output.println("     <input type=\"text\" name=\"message\" />");
    output.println("     <input type=\"submit\" value=\"Write!\" />");
    output.println("  </form>");
    output.println("</div>");
  }

  /**
   * HTML top boilerplate; put in a function so that I can use it for all the pages I come up with.
   * @param html where to write to; get this from the HTTP response.
   * @param title the title of the page, since that goes in the header.
   */
  private void printWritrPageStart(PrintWriter html, String title) {
    html.println("<!DOCTYPE html>"); // HTML5
    html.println("<html>");
    html.println("  <head>");
    html.println("    <title>"+title+"</title>");
    html.println("    "+metaURL);
    html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\""+getStaticURL("writr.css")+"\">");
    html.println("  </head>");
    html.println("  <body>");
    html.println("  <h1 class=\"logo\">Writr</h1>");
  }
  /**
   * HTML bottom boilerplate; close all the tags we open in printWritrPageStart.
   * @param html where to write to; get this from the HTTP response.
   */
  private void printWritrPageEnd(PrintWriter html) {
    html.println("  </body>");
    html.println("</html>");
  }

  /**
   * The main callback from Jetty.
   * @param resource what is the user asking for from the server?
   * @param jettyReq the same object as the next argument, req, just cast to a jetty-specific class (we don't need it).
   * @param req http request object -- has information from the user.
   * @param resp http response object -- where we respond to the user.
   * @throws IOException -- If the user hangs up on us while we're writing back or gave us a half-request.
   * @throws ServletException -- If we ask for something that's not there, this might happen.
   */
  @Override
  public void handle(String resource, Request jettyReq, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    System.out.println(jettyReq);

    String method = req.getMethod();
    String path = req.getPathInfo();
    if("POST".equals(method) && "/submit".equals(path)) {
      handleForm(req, resp);
      return;
    }

    try (PrintWriter html = resp.getWriter()) {
      printWritrPageStart(html, "Writr");

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
      printWritrPageEnd(html);
    }
  }

  /**
   * When a user submits (enter key) or pressed the "Write!" button, we'll get their request in here. This is called explicitly from handle, above.
   * @param req -- we'll grab the form parameters from here.
   * @param resp -- where to write their "success" page.
   * @throws IOException again, real life happens.
   */
  private void handleForm(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Map<String, String[]> parameterMap = req.getParameterMap();

    // if for some reason, we have multiple "message" fields in our form, just put a space between them, see Util.join.
    // Note that message comes from the name="message" parameter in our <input> elements on our form.
    String text = Util.join(parameterMap.get("message"));

    if(text != null) {
      // Good, got new message from form.
      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      messageList.add(new WritrMessage(text));

      // Respond!
      try (PrintWriter html = resp.getWriter()) {
        printWritrPageStart(html, "Writr: Submitted!");
        // Print actual redirect directive:
        html.println("<meta http-equiv=\"refresh\" content=\"3; url=front \">");

        // Thank you, link.
        html.println("<div class=\"body\">");
        html.println("<div class=\"thanks\">");
        html.println("<p>Thanks for your Submission!</p>");
        html.println("<a href=\"front\">Back to the front page...</a> (automatically redirect in 3 seconds).");
        html.println("</div>");
        html.println("</div>");

        printWritrPageEnd(html);

      } catch (IOException ignored) {
        // Don't consider a browser that stops listening to us after submitting a form to be an error.
      }

      return;
    }

    // user submitted something weird.
    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad user.");
  }
}
