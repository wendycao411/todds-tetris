import java.awt.Graphics;
import java.awt.Color;

//keep track of the current shape stuff
public class Shape {

  public static final int WIDTH = 10;
  public static final int HEIGHT = 20;
  public static final int BLOCK_SIZE = 30;

  // keep track of user's score here based on the number of collisions they have
  private static int score = 0;

  private int x;
  private int y;
  private static int normal = 600;
  private static int fast = 50;
  private static int shapeDelay = normal;
  private long beginTime;

  // keeps track of how many cells the block is to move horizontally
  private int move_x;
  private boolean collision;

  private int[][] coords;
  private Canvas board;
  private Color color;

  // constructor
  public Shape(int[][] coords, Canvas board, Color color) {
    // initializes the shape and colors and stuff to keep track of
    this.coords = coords;
    this.board = board;
    this.color = color;
    collision = false;
    move_x = 0;
    x = 4;
    y = 0;
  }

  // easily change the coordinates
  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  // reset the coordinates
  public void reset() {
    this.x = 4;
    this.y = 0;
    collision = false;
  }

  // moves the shape around or checks for collisions
  public void update() {
    if (collision) {

      score++;
      // fill color for board
      for (int row = 0; row < coords.length; row++) {
        for (int col = 0; col < coords[0].length; col++) {
          if (coords[row][col] != 0) {
            board.getBoard()[y + row][x + col] = color;
          }
        }
      }

      checkLine();
      // set current shape
      board.setCurrentShape();
      return;
    }

    // check moving horizontal
    boolean mvmt = true;
    // don't go out of bounds or overlap other blocks
    if (!(x + move_x + coords[0].length > 10) && !(x + move_x < 0)) {
      for (int row = 0; row < coords.length; row++) {
        for (int col = 0; col < coords[row].length; col++) {
          if (coords[row][col] != 0) {
            if (board.getBoard()[y + row][x + move_x + col] != null) {
              mvmt = false;
            }
          }
        }
      }
      if (mvmt) {
        x += move_x;
      }
    }
    move_x = 0;

    // for each certain amounts of time (delays) the block moves down a cell
    if (System.currentTimeMillis() - beginTime > shapeDelay) {
      // vertical movement
      // check out of bounds
      if (!(y + 1 + coords.length > HEIGHT)) {
        for (int row = 0; row < coords.length; row++) {
          for (int col = 0; col < coords[row].length; col++) {
            if (coords[row][col] != 0) {
              if (board.getBoard()[y + 1 + row][x + move_x + col] != null) {
                collision = true;
              }
            }
          }
        }
        if (!collision) {
          y++;
        }
      }
      // move down until it meets a wall or block
      else {
        collision = true;
      }
      beginTime = System.currentTimeMillis();
    }
  }

  // checks if there is a filled line to clear
  private void checkLine() {
    int bottomLine = board.getBoard().length - 1;
    // check from bottom to top, counts filled blocks
    for (int row = board.getBoard().length - 1; row > 0; row--) {
      int count = 0;
      for (int col = 0; col < board.getBoard()[0].length; col++) {
        if (board.getBoard()[row][col] != null) {
          count++;
        }
        // shift rows down
        board.getBoard()[bottomLine][col] = board.getBoard()[row][col];
      }

      // if not fully filled the bottom line shifts up
      if (count < board.getBoard()[0].length) {
        bottomLine--;
      }
    }
  }

  // checks if it can rotate the shapes around
  public void rotateShape() {
    // new matrix with the rotated shape
    int[][] rotatedShape = transpose(coords);
    reverseRows(rotatedShape);
    // check if it is out of bounds on the right side and bottom
    if ((x + rotatedShape[0].length > Canvas.WIDTH) || (y + rotatedShape.length > Canvas.HEIGHT)) {
      return;
    }

    // check for collision with other shapes before rotate
    for (int row = 0; row < rotatedShape.length; row++) {
      for (int col = 0; col < rotatedShape[row].length; col++) {
        if (rotatedShape[row][col] != 0) {
          if (board.getBoard()[y + row][x + col] != null) {
            return;
          }
        }
      }
    }
    coords = rotatedShape;
  }

  // rotates the matrix
  private int[][] transpose(int[][] matrix) {
    int[][] temp = new int[matrix[0].length][matrix.length];
    for (int row = 0; row < matrix.length; row++) {
      for (int col = 0; col < matrix[0].length; col++) {
        temp[col][row] = matrix[row][col];
      }
    }
    return temp;
  }

  // to rotate fully counterclockwise the matrix must be reversed
  private void reverseRows(int[][] matrix) {
    int middle = matrix.length / 2;
    for (int row = 0; row < middle; row++) {
      int[] temp = matrix[row];
      matrix[row] = matrix[matrix.length - row - 1];
      matrix[matrix.length - row - 1] = temp;
    }
  }

  public void render(Graphics g) {
    // draw the shape
    for (int row = 0; row < coords.length; row++) {
      for (int col = 0; col < coords[0].length; col++) {
        if (coords[row][col] != 0) {
          g.setColor(color);
          g.fillRect(col * BLOCK_SIZE + x * BLOCK_SIZE, row * BLOCK_SIZE + y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        }
      }
    }
  }

  public void speedUp() {
    shapeDelay = fast;
  }

  public void speedDown() {
    shapeDelay = normal;
  }

  public void moveRight() {
    move_x = 1;
  }

  public void moveLeft() {
    move_x = -1;
  }

  public int[][] getCoords() {
    return coords;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getScore() {
    return score;
  }

  public void resetScore() {
    score = 0;
  }
}