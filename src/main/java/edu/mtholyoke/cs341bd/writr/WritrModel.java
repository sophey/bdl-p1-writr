package edu.mtholyoke.cs341bd.writr;

import java.util.HashMap;
import java.util.Map;

public class WritrModel {

  // map of post ids and posts
  Map<Integer, WritrPost> posts;
  int nextPostID;

  public WritrModel() {
    posts = new HashMap<>();
    nextPostID = 1;
  }

  public WritrPost getPost(int id) {
    return posts.get(id);
  }

  public void addPost(WritrPost post) {
    posts.put(nextPostID++, post);
  }

  public int getNextPostID() {
    return nextPostID;
  }

  public Map<Integer, WritrPost> getPosts() {
    return posts;
  }
}