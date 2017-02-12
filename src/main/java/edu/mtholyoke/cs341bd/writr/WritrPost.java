package edu.mtholyoke.cs341bd.writr;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jfoley
 */
public class WritrPost implements Comparable<WritrPost> {
  /**
   * This is the timestamp of when this message was added -- when the
   * constructor gets called.
   * We assume this is close enough to when the user presses the submit
   * button for our uses.
   * <p>
   * It's a long because it's the number of milliseconds since 1960... how
   * computers tell time.
   */
  long timeStamp;
  /**
   * The text the user typed in.
   */
  String messageText;
  String userText;
  String titleText;
  List<WritrPost> comments;
  int uid;

  /**
   * Create a message and init its time stamp.
   *
   * @param text the text of the message.
   */
  public WritrPost(String text, String user, String title, int UID) {
    messageText = text;
    userText = user;
    titleText = title;
    comments = new LinkedList<>();
    timeStamp = System.currentTimeMillis();
    uid = UID;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public String getMessageText() {
    return messageText;
  }

  public String getUserText() {
    return userText;
  }

  public String getTitleText() {
    return titleText;
  }

  public List<WritrPost> getComments() {
    return comments;
  }

  public int getUid() {
    return uid;
  }

  /**
   * Sort newer messages to top by default. Maybe someday we'll sort in other
   * ways.
   *
   * @param o the other message to compare to.
   * @return comparator of (this, o).
   */
  @Override
  public int compareTo(@Nonnull WritrPost o) {
    return -Long.compare(timeStamp, o.timeStamp);
  }
}
