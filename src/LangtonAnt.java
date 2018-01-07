import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LangtonAnt extends JPanel {
    private int width, height, gridWidth, gridHeight, rows, cols;
    private static final long updateInterval = 1;
    private JFrame frame;
    private ArrayList<Grid> blackGrids;
    private ArrayList<Ant> ants = new ArrayList<>();
    private boolean drawGrid = false;

    private class Grid {
        int x;
        int y;

        public Grid(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Grid() {
            this.x = 0;
            this.y = 0;
        }
    }

    private class Ant {
        private Grid grid;
        private BufferedImage image;
        private Direction direction;
        private int imgWidth, imgHeight;

        public Ant(Grid grid, BufferedImage image, Direction direction) {
            this.grid = grid;
            this.image = image;
            this.imgWidth = (int)(gridWidth * .75);
            this.imgHeight = (int)(gridHeight * .75);
            this.direction = direction;
        }
    }

    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private void turnLeft(Ant ant) {
        switch (ant.direction) {
            case UP:
                ant.direction = Direction.LEFT;
                ant.grid.x--;
                break;
            case LEFT:
                ant.direction = Direction.DOWN;
                ant.grid.y++;
                break;
            case DOWN:
                ant.direction = Direction.RIGHT;
                ant.grid.x++;
                break;
            case RIGHT:
                ant.direction = Direction.UP;
                ant.grid.y--;
                break;
        }
    }

    private void turnRight(Ant ant) {
        switch (ant.direction) {
            case UP:
                ant.direction = Direction.RIGHT;
                ant.grid.x++;
                break;
            case LEFT:
                ant.direction = Direction.UP;
                ant.grid.y--;
                break;
            case DOWN:
                ant.direction = Direction.LEFT;
                ant.grid.x--;
                break;
            case RIGHT:
                ant.direction = Direction.DOWN;
                ant.grid.y++;
                break;
        }
    }

    public LangtonAnt(int width, int height, int rows, int cols) {
        this.width = width;
        this.height = height;
        this.rows = rows;
        this.cols = cols;
        this.gridWidth = width / rows;
        this.gridHeight = height / cols;
        blackGrids = new ArrayList<>();

        try {
            Ant ant1 = new Ant(new Grid(rows / 3, cols / 2), ImageIO.read(new File("src/ant.png")), Direction.UP);
            Ant ant2 = new Ant(new Grid(rows / 3 * 2, cols / 2), ImageIO.read(new File("src/ant.png")), Direction.UP);
            ants.add(ant1);
            ants.add(ant2);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        frame = new JFrame("Langton's Ant");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(width, height);

        frame.getContentPane().add(this);
        frame.setVisible(true);
    }

    private void drawGrids(Graphics g) {
        if(drawGrid) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    g.setColor(Color.WHITE);
                    g.fillRect(i * gridWidth, j * gridHeight, gridWidth, gridHeight);
                    g.setColor(Color.BLACK);
                    g.drawRect(i * gridWidth, j * gridHeight, gridWidth, gridHeight);
                }
            }
        }

        //Fill in black grids
        for(Grid grid : blackGrids) {
            fillGrid(grid, Color.BLACK, g);
        }
    }

    private void fillGrid(Grid grid, Color color, Graphics g) {
        g.fillRect(grid.x * gridWidth, grid.y * gridHeight, gridWidth, gridHeight);

        //If white, redraw grid outline
        if(color == Color.WHITE) {
            g.drawRect(grid.x * gridWidth, grid.y * gridHeight, gridWidth, gridHeight);
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.white);
        drawGrids(g);

        //Assign which ArrayList to use, and update each ant
        for(Ant ant : ants) {
            updateAnt(ant, g);
        }
    }

    private void updateAnt(Ant ant, Graphics g) {
        drawAnt(ant, g);

        boolean wasOnBlack = false;
        Grid gridToRemove = new Grid();

        //Check if ant is on a black grid
        for(Grid grid : blackGrids) {
            if(ant.grid.x == grid.x && ant.grid.y == grid.y) {
                wasOnBlack = true;
                gridToRemove = grid;
                break;
            }
        }

        if(wasOnBlack) {
            blackGrids.remove(gridToRemove);
            turnLeft(ant);
        } else {
            blackGrids.add(new Grid(ant.grid.x, ant.grid.y));
            turnRight(ant);
        }
    }

    private void drawAnt(Ant ant, Graphics g) {
        int x = ant.grid.x * gridWidth + ant.imgWidth / 2;
        int y = ant.grid.y * gridHeight + ant.imgHeight / 2;
        g.drawImage(ant.image, x, y, ant.imgWidth, ant.imgHeight, frame);
    }

    private void updateFrame() {
        frame.repaint();
    }

    public static void main(String[] args) {
        long currentTime = System.currentTimeMillis();
        LangtonAnt langtonAnt = new LangtonAnt(1060, 1060, 100, 100);
        langtonAnt.drawGrid = false;

        while(true) {
            if(System.currentTimeMillis() - currentTime >= langtonAnt.updateInterval) {
                langtonAnt.updateFrame();
                currentTime = System.currentTimeMillis();
            }
        }
    }
}
