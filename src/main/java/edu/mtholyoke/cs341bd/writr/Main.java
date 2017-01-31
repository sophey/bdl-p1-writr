package edu.mtholyoke.cs341bd.writr;

import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {
    // use port 1234 or the first argument specified to this program.
    int port = args.length > 0 ? Integer.parseInt(args[0]) : 1234;
    // hosting configuration: is this at the root of a webserve? default -- yes.
    String baseURL = args.length > 1 ? args[1] : "/";

    WritrServer app = new WritrServer(baseURL, port);
    try {
      app.run();
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }
}
