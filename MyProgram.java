import javax.swing.JFrame;

public class MyProgram {
  public static final int WIDTH = 450;
  public static final int HEIGHT = 650;
  private Canvas board;
  private JFrame frame;

  public MyProgram() {
    frame = new JFrame("Tetris");
    frame.setSize(WIDTH, HEIGHT);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setLocationRelativeTo(null);

    board = new Canvas();
    frame.add(board);
    frame.setVisible(true);

  }

  public static void main(String[] args) {
    new MyProgram();
  }
}