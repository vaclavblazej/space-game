package spacegame.controller;

import spacegame.Settings;
import spacegame.model.Model;
import spacegame.model.basics.Point;
import spacegame.model.things.Rectangle;
import spacegame.view.View;

import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Václav Blažej
 */
public class Input implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {

    private final Model model;
    private final Settings settings;
    private View view;
    private double scaleSpeed = 1.3;
    private Point<Double> startDrag = new Point<>(0d, 0d);
    private Boolean drag = false;
    private Map<Integer, Runnable> keyMap = new HashMap<>();
    private double speed = 3;

    public Input(View view, Model model, Settings settings) {
        this.view = view;
        this.model = model;
        this.settings = settings;
        keyMap.put(107, () -> view.zoomView(scaleSpeed)); // +
        keyMap.put(109, () -> view.zoomView(1 / scaleSpeed)); // -
        keyMap.put(68, () -> {
            final AffineTransform transform = settings.getViewTransform();
            transform.translate(-speed / transform.getScaleX(), 0.0);
        }); // a
        keyMap.put(65, () -> {
            final AffineTransform transform = settings.getViewTransform();
            transform.translate(+speed / transform.getScaleX(), 0.0);
        }); // d
        keyMap.put(87, () -> {
            final AffineTransform transform = settings.getViewTransform();
            transform.translate(0.0, +speed / transform.getScaleX());
        }); // w
        keyMap.put(83, () -> {
            final AffineTransform transform = settings.getViewTransform();
            transform.translate(0.0, -speed / transform.getScaleX());
        }); // s
        keyMap.put(37, view::prevState); // <-
        keyMap.put(39, view::nextState); // ->
        keyMap.put(67, view::center); // c
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        final int key = e.getKeyCode();
        if (keyMap.containsKey(key)) {
            keyMap.get(key).run();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
            model.addShape(new Rectangle(positionInView(e), 0d, 20d));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public Point<Double> positionInView(MouseEvent e) {
        return view.positionInView(new Point<>((double) e.getX(), (double) e.getY()));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        final Point<Double> point = new Point<>((double) e.getX(), (double) e.getY());
        if (e.getWheelRotation() == 1) {
            view.zoomView(1 / scaleSpeed, point);
        } else {
            view.zoomView(scaleSpeed, point);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
            if (drag) {
                Point<Double> now = positionInView(e);
                Point<Double> move = new Point<>(now.x - startDrag.x, now.y - startDrag.y);
                view.moveView(move);
            } else {
                drag = true;
                startDrag = positionInView(e);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        drag = false;
    }
}
