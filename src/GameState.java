import java.util.Set;
import java.util.HashSet;
import java.awt.Point;

public class GameState {

    private Set<Point> liveCells;

    public Set<Point> getLiveCells() {
        return new HashSet<Point>(liveCells);
    }

    public GameState() {
        liveCells = new HashSet<Point>();
    }

    public GameState(GameState original) {
        liveCells = new HashSet<Point>(original.liveCells);
    }

    // Returns eight points in Moore neighborhood of point
    private static Set<Point> getNeighbors(Point p) {
        Set<Point> neighbors = new HashSet<Point>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Point n = new Point(p.x + i, p.y + j);

                if (!p.equals(n)) {
                    neighbors.add(n);
                }
            }
        }

        return neighbors;
    }

    // Returns a new GameState with a point toggled
    public GameState togglePoint(Point p) {
        GameState next = new GameState(this);

        if (liveCells.contains(p)) {
            next.liveCells.remove(p);
        } else {
            next.liveCells.add(p);
        }

        return next;
    }

    // Returns the GameState after the next tick
    public GameState next() {
        GameState next = new GameState(this);

        Set<Point> toDie = new HashSet<Point>();
        Set<Point> toLive = new HashSet<Point>();

        // Dead cells next to live ones may or may not come alive,
        // so let's save them and process them in a second loop.
        Set<Point> toProcess = new HashSet<Point>();

        for (Point p : liveCells) {
            int liveNeighbors = 0;

            for (Point n : getNeighbors(p)) {
                if (liveCells.contains(n)) {
                    liveNeighbors++;
                } else {
                    toProcess.add(n);
                }
            }

            if (liveNeighbors < 2) {
                // Underpopulation
                toDie.add(p);
            } else if (liveNeighbors > 3) {
                // Overpopulation
                toDie.add(p);
            }
        }

        // Second loop, process dead cells
        for (Point d : toProcess) {
            int liveNeighbors = 0;

            for (Point n : getNeighbors(d)) {
                if (liveCells.contains(n)) {
                    liveNeighbors++;
                }
            }

            if (liveNeighbors == 3) {
                // Reproduction
                toLive.add(d);
            }
        }

        // Perform iteration
        next.liveCells.removeAll(toDie);
        next.liveCells.addAll(toLive);

        return next;
    }

}
