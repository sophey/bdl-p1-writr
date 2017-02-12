package edu.mtholyoke.cs341bd.writr;

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
  public static void appendHTML(StringBuilder output, WritrPost post) {
    output
        .append("<div class=\"message\">")
        .append("<span class=\"datetime\">")
        .append(Util.dateToEST(post.getTimeStamp()))
        .append("</span><br><span>User: ")
        .append(post.getUserText())
        .append("</span><br><span>Title: ")
        .append(post.getTitleText())
        .append("</span><br><span>Message: ")
        .append(post.getMessageText())
        .append("</span>")
        .append("</div>");
  }

}
