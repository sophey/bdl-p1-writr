package edu.mtholyoke.cs341bd.writr;

import javax.annotation.Nullable;

/**
 * @author jfoley
 */
public class Util {
  @Nullable
  public static String join(@Nullable String[] array) {
    if(array == null) return null;

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < array.length; i++) {
      if(i > 0) sb.append(' ');
      sb.append(array[i]);
    }
    return sb.toString();
  }
}
