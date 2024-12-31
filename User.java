public class User {
  // keeps track of the player's name and their final score
  private String user;
  private int score;

  public User(String user, int score) {
    this.user = user;
    this.score = score;
  }

  public int getScore() {
    return score;
  }

  public String getName() {
    return user;
  }

  // to sort the array we must find the highest scoring players
  public int compareTo(User user2) {
    int score2 = user2.getScore();
    if (score2 > score) {
      return 1;
    }
    return -1;
  }

  public boolean equalTo(User user2) {
    int score2 = user2.getScore();
    String name2 = user2.getName();

    if (name2.equals(user) && score2 == score) {
      return true;
    }

    return false;
  }

}