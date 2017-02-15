package edu.mtholyoke.cs341bd.writr;

import java.io.PrintWriter;

/**
 * Created by Sophey on 2/10/17.
 */
public class WritrView {

  /**
   * Rather than give a PrintWriter here, we'll use a StringBuilder, so we
   * can quickly build up a string from all of the messages at once. I
   * mostly did this a different way just to show it.
   *
   * @param output a stringbuilder object, to which we'll add our HTML
   *               representation.
   * @param post   a WritrPost object, which holds the post content
   */
  public static void displayPost(StringBuilder output, WritrPost post) {
    output
        .append("<div class=\"message\">")
        .append("<span class=\"datetime\">")
        .append("<a href=/uid/" + post.getUid() + ">" + Util.dateToEST(post
            .getTimeStamp()) + "</a>")
        .append("</span><br><span>User: ")
        .append(post.getUserText())
        .append("</span><br><span>Title: ")
        .append(post.getTitleText())
        .append("</span><br><span>Message: ")
        .append(post.getMessageText())
        .append("</span>")
        .append("</div>");
  }

  /**
   * Rather than give a PrintWriter here, we'll use a StringBuilder, so we
   * can quickly build up a string from all of the messages at once. I
   * mostly did this a different way just to show it.
   *
   * @param output a stringbuilder object, to which we'll add our HTML
   *               representation.
   * @param post   a WritrPost object, which holds the post content
   */
  public static void displayComment(StringBuilder output, WritrPost post) {
    output
        .append("<div class=\"message\">")
        .append("<span class=\"datetime\">")
        .append(Util.dateToEST(post.getTimeStamp()))
        .append("</span><br><span>User: ")
        .append(post.getUserText())
        .append("</span><br><span>Title: ")
        .append(post.getTitleText())
        .append("</span><br><span>Comment: ") //changed this
        .append(post.getMessageText())
        .append("</span>")
        .append("</div>");
  }

  /**
   * Made this a function so that we can have the submit form at the top &
   * bottom of the page.
   * <a href="http://www.w3schools.com/html/html_forms.asp">Tutorial about
   * Forms</a>
   *
   * @param output where to write our HTML to
   */
  public static void printWritrForm(PrintWriter output) {
    output.println("<div class=\"form\">");
    output.println("  <form action=\"submit\" method=\"POST\">");
    output.println("  <label>User: <input type=\"text\" name=\"user\" " +
        "/></label>"); //changed this
    output.println(" <label><br>Title: <input type=\"text\" name=\"title\" " +
        "/></label>");
    output.println("  <label><br>Message: <input type=\"text\" " +
        "name=\"message\" /></label>");
    output.println("     <br><input type=\"submit\" value=\"Write!\" />");
    output.println("  </form>");
    output.println("</div>");
  }

  /**
   * Made this a function so that we can have the submit form at the top &
   * bottom of the page.
   * <a href="http://www.w3schools.com/html/html_forms.asp">Tutorial about
   * Forms</a>
   *
   * @param output where to write our HTML to
   */
  public static void printCommentForm(PrintWriter output, int uid) {
    output.println("<div class=\"form\">");
    output.println("  <form action=\"submitComment/" + uid + "\" " +
        "method=\"POST\">");
    output.println("  <labelComment>User: <input type=\"text\" name=\"user\" " +
        "/></labelComment>"); //changed this
    output.println(" <labelComment><br>Title: <input type=\"text\" name=\"title\" " +
        "/></labelComment>");
    output.println("  <labelComment><br>Comment: <input type=\"text\" " +   //changed
        "name=\"message\" /></labelComment>");
    output.println("     <br><input type=\"submit\" value=\"Write!\" />");
    output.println("  </form>");
    output.println("</div>");
  }

  /**
   * HTML top boilerplate; put in a function so that I can use it for all the
   * pages I come up with.
   *
   * @param html  where to write to; get this from the HTTP response.
   * @param title the title of the page, since that goes in the header.
   */
  public static void printWritrPageStart(PrintWriter html, String title,
                                         String metaURL, String staticURL) {
    html.println("<!DOCTYPE html>"); // HTML5
    html.println("<html>");
    html.println("  <head>");
    html.println("    <title>" + title + "</title>");
    html.println("    " + metaURL);
    html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\"" +
        staticURL + "\">");
    html.println("  </head>");
    html.println("  <body>");
    html.println("  <a href=\"/\"><h1 class=\"logo\">Writr</h1></a>");
  }

  //getStaticURL("writr.css")

  /**
   * HTML bottom boilerplate; close all the tags we open in printWritrPageStart.
   *
   * @param html where to write to; get this from the HTTP response.
   */
  public static void printWritrPageEnd(PrintWriter html) {
    html.println("  </body>");
    html.println("</html>");
  }

}
