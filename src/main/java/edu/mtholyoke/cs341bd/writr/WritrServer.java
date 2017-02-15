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
import java.util.*;

/**
 * @author jfoley
 */
public class WritrServer extends AbstractHandler {
  String metaURL;
  Server jettyServer;
  WritrModel model;

  public WritrServer(String baseURL, int port) throws IOException {
    this.metaURL = "<base href=\"" + baseURL + "\">";
    jettyServer = new Server(port);

    // We create a ContextHandler, since it will catch requests for us under
    // a specific path.
    // This is so that we can delegate to Jetty's default ResourceHandler to
    // serve static files, e.g. CSS & images.
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

    // create model
    model = new WritrModel();
  }

  /**
   * Once everything is set up in the constructor, actually start the server
   * here:
   *
   * @throws Exception if something goes wrong.
   */
  public void run() throws Exception {
    jettyServer.start();
    jettyServer.join(); // wait for it to finish here! We're using threads
    // behind the scenes; so this keeps the main thread around until
    // something can happen!
  }

  public String getStaticURL(String resource) {
    return "static/" + resource;
  }

  /**
   * The main callback from Jetty.
   *
   * @param resource what is the user asking for from the server?
   * @param jettyReq the same object as the next argument, req, just cast to
   *                 a jetty-specific class (we don't need it).
   * @param req      http request object -- has information from the user.
   * @param resp     http response object -- where we respond to the user.
   * @throws IOException      -- If the user hangs up on us while we're
   *                          writing back or gave us a half-request.
   * @throws ServletException -- If we ask for something that's not there,
   *                          this might happen.
   */
  @Override
  public void handle(String resource, Request jettyReq, HttpServletRequest
      req, HttpServletResponse resp) throws IOException, ServletException {
    System.out.println(jettyReq);

    String method = req.getMethod();
    String path = req.getPathInfo();
    if ("POST".equals(method)) {
      if ("/submit".equals(path)) {
        handleForm(req, resp);
        return;
      } else if (path.contains("/submitComment")) {
        handleCommentForm(req, resp, path);
        return;
      }
    } else if ("GET".equals(method) && path.contains("/uid")) {
      handlePostPage(path, resp);
      return;
    }

    try (PrintWriter html = resp.getWriter()) {
    	
      WritrView.printWritrPageStart(html, "Writr", metaURL, getStaticURL
          ("writr.css"));

      // Print the form at the top of the page
      WritrView.printWritrForm(html);

      // Print all of our messages
      html.println("<div class=\"body\">");

      // get a copy to sort:
      ArrayList<Integer> messages = new ArrayList<>(model.getPosts().keySet());
      Collections.sort(messages);
      Collections.reverse(messages);

      StringBuilder messageHTML = new StringBuilder();
      for (int postId : messages) {
        WritrPost writrPost = model.getPost(postId);
        WritrView.displayPost(messageHTML, writrPost);
      }
      html.println(messageHTML);
      html.println("</div>");

      // when we have a big page,
      if (messages.size() > 25) {
        // Print the submission form again at the bottom of the page
        WritrView.printWritrForm(html);
      }
     
      WritrView.printWritrPageEnd(html);
    	
    	
    }
  }

  //I modified this
  private void handlePostPage(String path, HttpServletResponse resp) throws
      IOException {
    try (PrintWriter html = resp.getWriter()) {
      WritrView.printWritrPageStart(html, "Writr", metaURL, getStaticURL
          ("writr.css"));

      int uid = Integer.parseInt(path.substring(5));

      // Print all of our messages
      html.println("<div class=\"body\">");

      StringBuilder messageHTML = new StringBuilder();

      WritrPost writrPost = model.getPost(uid);
      WritrView.displayPost(messageHTML, writrPost);

      html.println(messageHTML);

      // Print the form at the top of the page
      WritrView.printCommentForm(html, uid);

      messageHTML = new StringBuilder();

      List<WritrPost> comments = writrPost.getComments();
      
      for (WritrPost post : comments) {
    	  
        WritrView.displayComment(messageHTML, post);
      }
    
      html.println(messageHTML);
      html.println("</div>");
    
      WritrView.printWritrPageEnd(html);
    }
  }

  /**
   * When a user submits (enter key) or pressed the "Write!" button, we'll
   * get their request in here. This is called explicitly from handle, above.
   *
   * @param req  -- we'll grab the form parameters from here.
   * @param resp -- where to write their "success" page.
   * @throws IOException again, real life happens.
   */
  private void handleCommentForm(HttpServletRequest req, HttpServletResponse
      resp, String path) throws IOException {
    Map<String, String[]> parameterMap = req.getParameterMap();

    // if for some reason, we have multiple "message" fields in our form,
    // just put a space between them, see Util.join.
    // Note that message comes from the name="message" parameter in our
    // <input> elements on our form.
    String text = Util.join(parameterMap.get("message"));
    String user = Util.join(parameterMap.get("user"));
    String title = Util.join(parameterMap.get("title"));

    if (text != null && user != null && title != null) {
      // Good, got new message from form.
      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      WritrPost writrPost = model.getPost(Integer.parseInt(path.substring(15)));

      writrPost.addComment(new WritrPost(text, user, title, 0));

      submitPage(resp);

      return;
    }

    // user submitted something weird.
    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad user.");
  }

  /**
   * When a user submits (enter key) or pressed the "Write!" button, we'll
   * get their request in here. This is called explicitly from handle, above.
   *
   * @param req  -- we'll grab the form parameters from here.
   * @param resp -- where to write their "success" page.
   * @throws IOException again, real life happens.
   */
  private void handleForm(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    Map<String, String[]> parameterMap = req.getParameterMap();

    // if for some reason, we have multiple "message" fields in our form,
    // just put a space between them, see Util.join.
    // Note that message comes from the name="message" parameter in our
    // <input> elements on our form.
    String text = Util.join(parameterMap.get("message"));
    String user = Util.join(parameterMap.get("user"));
    String title = Util.join(parameterMap.get("title"));

    if (text != null && user != null && title != null) {
      // Good, got new message from form.
      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      model.addPost(new WritrPost(text, user, title, model.getNextPostID()));

      submitPage(resp);

      return;
    }

    // user submitted something weird.
    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad user.");
  }

  private void submitPage(HttpServletResponse resp) {
    // Respond!
    try (PrintWriter html = resp.getWriter()) {
      WritrView.printWritrPageStart(html, "Writr: Submitted!", metaURL,
          getStaticURL("writr.css"));
      // Print actual redirect directive:
      html.println("<meta http-equiv=\"refresh\" content=\"3; url=front \">");

      // Thank you, link.
      html.println("<div class=\"body\">");
      html.println("<div class=\"thanks\">");
      html.println("<p>Thanks for your Submission!</p>");
      html.println("<a href=\"front\">Back to the front page...</a> " +
          "(automatically redirect in 3 seconds).");
      html.println("</div>");
      html.println("</div>");

      WritrView.printWritrPageEnd(html);

    } catch (IOException ignored) {
      // Don't consider a browser that stops listening to us after
      // submitting a form to be an error.
    }
  }

}
