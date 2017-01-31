package edu.mtholyoke.cs341bd.writr;

import javax.annotation.Nonnull;

/**
 * @author jfoley
 */
public class WritrMessage implements Comparable<WritrMessage> {
  long timeStamp;
  String messageText;

  /**
   * Create a message and init its time stamp.
   * @param text the text of the message.
   */
  public WritrMessage(String text) {
    messageText = text;
    timeStamp = System.currentTimeMillis();
  }

  public void appendHTML(StringBuilder output) {
    output
        .append("<div class=\"message\">")
        .append("<span class=\"datetime\">").append(Util.dateToEST(timeStamp)).append("</span>")
        .append(messageText)
        .append("</div>");
  }

  /**
   * Sort newer messages to top by default.
   * @param o the other message to compare to.
   * @return comparator of (this, o).
   */
  @Override
  public int compareTo(@Nonnull WritrMessage o) {
    return -Long.compare(timeStamp, o.timeStamp);
  }
}
