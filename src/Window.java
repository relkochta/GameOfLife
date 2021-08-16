import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.awt.event.MouseAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.util.Set;

public class Window implements Interface {
    
    private static final int DEFAULT_WIDTH = 1400;
    private static final int DEFAULT_HEIGHT = 900;
    private static final int CELL_SIZE = 40;
    private static final int BORDER_SIZE = 2;
    private static final String TITLE = "Conway's Game of Life";

    private JFrame frame;
    private Canvas canvas;
    private int width, height;
    private Point reference;
    private Game game;

    public Window(Game game) {
        this(game, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public Window(Game game, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
        reference = new Point(0, 0);
        initialize();
    }

    private void initialize() {
        Dimension size = new Dimension(width, height);
        
        frame = new JFrame(TITLE);
        frame.setSize(size);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.addComponentListener(new WindowListener());

        canvas = new Canvas();
        canvas.setMinimumSize(size);
        canvas.setMaximumSize(size);
        canvas.setPreferredSize(size);
        canvas.addMouseListener(new ClickListener());

        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public synchronized void render(GameState gs) {
        Set<Point> liveCells = gs.getLiveCells();

        if (canvas.getBufferStrategy() == null) {
            canvas.createBufferStrategy(3);
        }

        BufferStrategy bs = canvas.getBufferStrategy();
        Graphics g = bs.getDrawGraphics();

        int rows = height / CELL_SIZE;
        int columns = width / CELL_SIZE;

        for (int j = 0; j <= rows; j++) {
            for (int i = 0; i <= columns; i++) {
                if (liveCells.contains(new Point(i + reference.x, j + reference.y))) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.WHITE);
                }

                // Draw Block Itself
                g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                // Draw Grid Portion
                g.setColor(Color.GRAY);
                g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, BORDER_SIZE);
                g.fillRect(i * CELL_SIZE, j * CELL_SIZE, BORDER_SIZE, CELL_SIZE);
            }
        }

        bs.show();
        g.dispose();
    }

    private class ClickListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                int x = (e.getX() / CELL_SIZE) + reference.x;
                int y = (e.getY() / CELL_SIZE) + reference.y;


                game.togglePoint(new Point(x, y));
            } else if (SwingUtilities.isRightMouseButton(e)) {
                game.pause();
            }
        }

    }

    private class WindowListener extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent c) {
            width = frame.getWidth();
            height = frame.getHeight();
            if (game.running()) {
                render(game.getGameState());
            }
        }

    }

}
