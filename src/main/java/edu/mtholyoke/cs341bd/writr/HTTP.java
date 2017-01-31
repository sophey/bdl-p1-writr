package edu.mtholyoke.cs341bd.writr;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author jfoley
 */
public class HTTP {
  public static void setupRedirectPage(String targetPath, HttpServletResponse resp) {
    try (PrintWriter html = resp.getWriter()) {
      html.println("<html>");
      html.println("  <head>");
      html.println("    <title>Writr: Thanks for your Submission</title>");
      html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\"static/writr.css\">");

      // Print actual redirect directive:
      html.println("<meta http-equiv=\"refresh\" content=\"0; url="+targetPath+"\">");

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
