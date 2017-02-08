package edu.mtholyoke.cs341bd.writr;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jfoley
 */
public class WritrPost implements Comparable<WritrPost> {
  /**
   * This is the timestamp of when this message was added -- when the constructor gets called.
   * We assume this is close enough to when the user presses the submit button for our uses.
   *
   * It's a long because it's the number of milliseconds since 1960... how computers tell time.
   */
  long timeStamp;
  /** The text the user typed in. */
  String messageText;
  String userText;
  String titleText;
  List<WritrPost> comments;

  /**
   * Create a message and init its time stamp.
   * @param text the text of the message.
   */
  public WritrPost(String text, String user, String title) {
    messageText = text;
    userText = user;
    titleText = title;
    comments = new LinkedList<>();
    timeStamp = System.currentTimeMillis();
  }

//  /**
//   * Rather than give a PrintWriter here, we'll use a StringBuilder, so we can quickly build up a string from all of the messages at once. I mostly did this a different way just to show it.
//   * @param output a stringbuilder object, to which we'll add our HTML representation.
//   */
//  public void appendHTML(StringBuilder output) {
//    output
//        .append("<div class=\"message\">")
//        .append("<span class=\"datetime\">").append(Util.dateToEST(timeStamp)).append("</span>")
//        .append(messageText)
//        .append("</div>");
//  }

  /**
   * Sort newer messages to top by default. Maybe someday we'll sort in other ways.
   *
   * @param o the other message to compare to.
   * @return comparator of (this, o).
   */
  @Override
  public int compareTo(@Nonnull WritrPost o) {
    return -Long.compare(timeStamp, o.timeStamp);
  }
}
