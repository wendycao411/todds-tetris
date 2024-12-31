import javax.swing.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Canvas extends JComponent {

  // these variables control the state of the game
  // 5 possible states for the user to be in: playing, pause, end game, main menu,
  // instructions, leaderboard
  private static int play = 0;
  private static int pause = 1;
  private static int gameover = 2;
  private static int titleScreen = 3;
  private static int instructions = 4;
  private static int leaderboard = 5;

  // this arraylist will keep track of the scores and names of players to put on
  // the leaderboard
  private static ArrayList<User> scores = new ArrayList<User>();

  // begin on title screen
  private static int state = titleScreen;

  // the player's name for the current game
  private String name;

  // frames per second (how frequently the game screen updates)
  private static int FPS = 60;
  private static int delay = 1000 / FPS;

  // size of the playing board
  public static final int WIDTH = 10;
  public static final int HEIGHT = 20;
  // size of a cell in the playing board
  public static final int BLOCK_SIZE = 30;

  private Timer looper;
  private Color[][] board;

  // the colors of the tetronimoes
  private static Color[] colors = { Color.decode("#FE0000"), Color.decode("#FE8100"), Color.decode("#FEDD00"),
      Color.decode("#00FE08"), Color.decode("#0025FE"), Color.decode("#9200FE"), Color.decode("#FE00AB") };

  // array with all the possible shapes of the tetronimoes
  private static Shape[] shapes = new Shape[7];
  private Shape currentShape;

  // constructor
  public Canvas() {

    // 1 represents a cell being colored, 0 represents an empty cell
    // each tetronimo has its own color
    shapes[0] = new Shape(new int[][] {
        { 1, 1, 1, 1 } // I-shape
    }, this, colors[0]);

    shapes[1] = new Shape(new int[][] {
        { 1, 1, 1 },
        { 0, 1, 0 }, // T-shape
    }, this, colors[1]);

    shapes[2] = new Shape(new int[][] {
        { 1, 1, 1 },
        { 1, 0, 0 }, // L-shape
    }, this, colors[2]);

    shapes[3] = new Shape(new int[][] {
        { 1, 1, 1 },
        { 0, 0, 1 }, // J-shape
    }, this, colors[3]);

    shapes[4] = new Shape(new int[][] {
        { 0, 1, 1 },
        { 1, 1, 0 }, // S-shape
    }, this, colors[4]);

    shapes[5] = new Shape(new int[][] {
        { 1, 1, 0 },
        { 0, 1, 1 }, // S-shape
    }, this, colors[5]);

    shapes[6] = new Shape(new int[][] {
        { 1, 1 },
        { 1, 1 }, // J-shape
    }, this, colors[6]);

    currentShape = shapes[0];

    // had to search up how to use a timer, but it'll basically update the screen
    // based on the fps
    looper = new Timer(delay, new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        update();
        repaint();
      }
    });
    looper.start();

    // the title screen relies on mouse movements
    class CanvasMouseListener implements MouseListener {

      // overriding the inherited method
      public void mouseClicked(MouseEvent e) {
        // coordinates of the mouse
        int x = e.getX();
        int y = e.getY();
        System.out.println(x);
        System.out.println(y);

        // general area of the "start new game" button
        if (state == titleScreen && x >= 130 && x <= 315 && y >= 380 && y <= 410) {
          name = JOptionPane.showInputDialog("Enter your name");
          // if they press cancel or there is no name the game doesn't play
          if (name != null && name.length() > 0) {
            board = new Color[HEIGHT][WIDTH];
            currentShape.resetScore();
            state = play;
          }
        }

        // general area of the instructions button
        else if (state == titleScreen && x >= 60 && x <= 315 && y >= 490 && y <= 530) {
          state = instructions;
        }

        // general area of the leaderboard button
        else if (state == titleScreen && x >= 60 && x <= 340 && y >= 440 && y <= 480) {
          state = leaderboard;
        }

        // back to main menu buttons
        else if ((state == instructions || state == gameover || state == leaderboard) && x >= 40 && x <= 340 && y >= 570
            && y <= 600) {
          state = titleScreen;
        }

      }

      public void mousePressed(MouseEvent e) {
      }

      public void mouseReleased(MouseEvent e) {
      }

      public void mouseEntered(MouseEvent e) {
      }

      public void mouseExited(MouseEvent e) {
      }

    }

    addMouseListener(new CanvasMouseListener());

    // keyboard movements needed to play tetris
    class CanvasKeyListener implements KeyListener {
      public void keyTyped(KeyEvent e) {
      }

      public void keyPressed(KeyEvent e) {
        // down arrow means speed up the block's falling
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
          currentShape.speedUp();
        }

        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
          currentShape.moveRight();
        }

        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
          currentShape.moveLeft();
        }

        else if (e.getKeyCode() == KeyEvent.VK_UP) {
          currentShape.rotateShape();
        }

        // pause game
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
          if (state == play) {
            state = pause;
          } else if (state == pause) {
            state = play;
          }
        }
      }

      // return to normal after down key pressed
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
          currentShape.speedDown();
        }
      }
    }

    addKeyListener(new CanvasKeyListener());
    setFocusable(true);
    requestFocus();
  }

  // check if lines are getting cleared and/or get new shape
  private void update() {
    if (state == play) {
      currentShape.update();
    }

  }

  // get a random new shape, check if game over before placing it
  public void setCurrentShape() {
    int randomidx = (int) (Math.random() * 7);
    currentShape = shapes[randomidx];
    currentShape.reset();
    checkGameOver();
  }

  // checks if the shape getting placed has any empty spaces to get placed in - if
  // not, game over
  private void checkGameOver() {
    int[][] coords = currentShape.getCoords();
    for (int row = 0; row < coords.length; row++) {
      for (int col = 0; col < coords[0].length; col++) {
        if (coords[row][col] != 0) {
          if (board[row + currentShape.getY()][col + currentShape.getX()] != null) {
            state = gameover;
          }
        }
      }
    }
  }

  // overriding method from Graphics package
  // a java error told me to make it a protected method
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // create the first window
    g.setColor(Color.black);
    g.fillRect(0, 0, getWidth(), getHeight());

    if (state == titleScreen) {
      drawTitleScreen(g);
    }

    else if (state == instructions) {
      instructionScreen(g);
    }

    else if (state == leaderboard) {
      topScore(g);
    }

    else {
      // start playing the game
      currentShape.render(g);

      for (int row = 0; row < board.length; row++) {
        for (int col = 0; col < board[row].length; col++) {
          // painting the board with tetronimo colors
          if (board[row][col] != null) {
            g.setColor(board[row][col]);
            g.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
          }

        }
      }

      // draw the board lines
      g.setColor(Color.white);
      for (int row = 0; row < HEIGHT + 1; row++) {
        g.drawLine(0, BLOCK_SIZE * row, BLOCK_SIZE * WIDTH, BLOCK_SIZE * row);
      }

      for (int col = 0; col < WIDTH + 1; col++) {
        g.drawLine(col * BLOCK_SIZE, 0, col * BLOCK_SIZE, BLOCK_SIZE * HEIGHT);
      }

      // keep track of player score
      // i couldn't use the drawString method without converting the int score to a
      // string so i appended it to an empty string
      String score = "";
      score += currentShape.getScore();
      g.setFont(g.getFont().deriveFont(Font.BOLD, 30F));

      g.drawString(score, 350, 200);
      g.drawString("SCORE:", 310, 160);

      if (state == gameover) {
        endScreen(g);
      }

      if (state == pause) {
        g.setColor(Color.red);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 50F));
        g.drawString("GAME PAUSED", 10, 200);
      }
    }
  }

  public void drawTitleScreen(Graphics g) {
    g.setColor(Color.decode("#B7EB83"));
    g.fillRect(0, 0, MyProgram.WIDTH, MyProgram.HEIGHT);

    g.setFont(g.getFont().deriveFont(Font.BOLD, 30F));
    String text = "TODD'S";
    int x = 160;
    int y = 120;

    g.setColor(Color.black);
    g.drawString(text, x + 3, y + 3);
    g.setColor(Color.white);
    g.drawString(text, x, y);

    g.setFont(g.getFont().deriveFont(Font.BOLD, 96F));
    text = "TETRIS";
    x = 35;
    y = 220;

    g.setColor(Color.black);
    g.drawString(text, x + 5, y + 5);
    g.setColor(Color.white);
    g.drawString(text, x, y);

    // menu
    g.setColor(Color.DARK_GRAY);
    g.setFont(g.getFont().deriveFont(Font.BOLD, 30F));

    text = "NEW GAME";
    g.drawString(text, 130, 410);

    text = "LEADERBOARD";
    g.drawString(text, 95, 470);

    text = "INSTRUCTIONS";
    g.drawString(text, 100, 530);

    Image phrog = new ImageIcon("todd and phrog.png").getImage();
    g.drawImage(phrog, 150, 230, 150, 150, null);

  }

  public void instructionScreen(Graphics g) {
    g.setColor(Color.black);
    g.fillRect(0, 0, MyProgram.WIDTH, MyProgram.HEIGHT);
    Image img = new ImageIcon("instructions.png").getImage();
    g.drawImage(img, 15, 110, 400, 400, null);
    g.setFont(g.getFont().deriveFont(Font.BOLD, 30F));
    g.drawString("SPACEBAR = PAUSE", 57, 140);

    Image angy = new ImageIcon("angry phrog.png").getImage();
    g.drawImage(angy, 250, 400, 200, 200, null);
    g.setFont(g.getFont().deriveFont(Font.BOLD, 10F));
    g.drawString("worship the phrog", 300, 500);

    g.setFont(g.getFont().deriveFont(Font.BOLD, 30F));
    g.setColor(Color.white);
    g.drawString("BACK TO MAIN MENU", 40, 600);
  }

  public void endScreen(Graphics g) {
    g.setColor(Color.black);
    g.fillRect(0, 0, MyProgram.WIDTH, MyProgram.HEIGHT);
    g.setFont(g.getFont().deriveFont(Font.BOLD, 50F));
    g.setColor(Color.white);
    g.drawString("GAME OVER", 50, 140);
    g.setFont(g.getFont().deriveFont(Font.BOLD, 30F));
    g.drawString("SCORE:", 150, 200);
    int score = currentShape.getScore();
    String scoreStr = "";
    scoreStr += score;
    g.drawString(scoreStr, 190, 250);

    Image oop = new ImageIcon("oop phrog.png").getImage();
    g.drawImage(oop, 150, 400, 200, 200, null);

    g.drawString("BACK TO MAIN MENU", 40, 600);

    // update the arraylist with scores and names
    User player = new User(name, score);
    scores.add(player);
    sort(scores);
  }

  // using insertion sort to sort the score arraylist from greatest to least
  public static void sort(ArrayList<User> list) {
    for (int i = 0; i < list.size(); i++) {
      User cur = list.get(i);
      int j = i - 1;
      while (j > -1 && cur.compareTo(list.get(j)) < 0) {
        list.set(j + 1, list.get(j--));
        list.set(j + 1, cur);
      }
    }
  }

  // creating a leaderboard
  public void topScore(Graphics g) {
    g.setColor(Color.black);
    g.fillRect(0, 0, MyProgram.WIDTH, MyProgram.HEIGHT);

    g.setColor(Color.white);
    g.setFont(g.getFont().deriveFont(Font.BOLD, 50));
    g.drawString("LEADERBOARD", 20, 100);

    int num = 0;
    int y = 200;

    // remove duplicate scores that may occur because of the timer loop my code has
    for (int i = 0; i < scores.size() - 1; i++) {
      for (int j = i + 1; j < scores.size(); j++) {
        if (scores.get(i).equalTo(scores.get(j))) {
          scores.remove(j);
        }
      }
    }

    g.setFont(g.getFont().deriveFont(Font.BOLD, 30));

    // get only the first 3 scores from the arraylist
    while (num < 3 && num < scores.size()) {
      int score = scores.get(num).getScore();
      String toprint = "";
      String name = scores.get(num).getName();
      toprint += name + ": " + score;
      g.drawString(toprint, 100, y);

      num++;
      y += 50;

    }

    Image phrog = new ImageIcon("phrog.png").getImage();
    g.drawImage(phrog, 15, 400, 200, 200, null);

    g.setFont(g.getFont().deriveFont(Font.BOLD, 30F));
    g.drawString("BACK TO MAIN MENU", 40, 600);

  }

  // accessor methods
  public Color[][] getBoard() {
    return board;
  }

}