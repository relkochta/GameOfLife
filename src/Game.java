import java.awt.Point;

public class Game implements Runnable {
    
    private Interface iface;
    private boolean running;
    private Thread thread;
    private final int tickLength;
    private GameState gs;

    public enum InterfaceType {
        WINDOW,
        COMMANDLINE,
    }

    public Game(int tickLength, InterfaceType ifaceType) {
        this.tickLength = tickLength;
        initializeInterface(ifaceType);
        gs = new GameState();
    }

    public boolean running() {
        return running;
    }

    public GameState getGameState() {
        return new GameState(gs);
    }

    public void togglePoint(Point p) {
        gs = gs.togglePoint(p);
        iface.render(gs);
    }

    public void initializeInterface(InterfaceType ifaceType) {
        if (ifaceType == InterfaceType.WINDOW) {
            iface = new Window(this);
        } else if (ifaceType == InterfaceType.COMMANDLINE) {
            System.err.println("Unimplemented");
            return;
        }
    }

    public synchronized boolean pause() {
        running = !running;
        return !running;
    }

    @Override
    public void run() {
        while (true) {
            if (running) {
                gs = gs.next();
            }
            iface.render(gs);
            try {
                Thread.sleep(tickLength);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
        }

        running = true;
        thread.start();
    }

}
