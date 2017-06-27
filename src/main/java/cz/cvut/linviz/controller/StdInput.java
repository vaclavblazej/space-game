package cz.cvut.linviz.controller;

import cz.cvut.linviz.model.Model;
import cz.cvut.linviz.model.basics.Ellipse;
import cz.cvut.linviz.model.basics.Line;
import cz.cvut.linviz.model.basics.Point;
import cz.cvut.linviz.model.basics.Polygon;
import cz.cvut.linviz.model.things.BaseShape;
import cz.cvut.linviz.model.things.Rectangle;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Václav Blažej
 */
public class StdInput {

    private static boolean background = false;

    public static void loadInput(Model model, InputStream in) {
        System.out.println("Getting the input from commandline");
        final InputStreamReader reader = new InputStreamReader(in);
        final Scanner scanner = new Scanner(reader);

        loop:
        while (true) {
            final String command = scanner.next();
            switch (command) {
                case "exit": {
                    break loop;
                }
                case "rt": {
                    addRectangle(scanner, model);
                    break;
                }
                case "pg": {
                    addPolygon(scanner, model);
                    break;
                }
                case "pgp": {
                    addPolygonHalfPlane(scanner, model);
                    break;
                }
                case "el": {
                    addEllipse(scanner, model);
                    break;
                }
                case "ln": {
                    addLine(scanner, model);
                    break;
                }
                case "dl": {
                    addGeneralLine(scanner, model);
                    break;
                }
                case "frame": {
                    model.addFrame();
                    break;
                }
                case "pause": {
                    model.addSubframe();
                    break;
                }
                case "background": {
                    background = true;
                    break;
                }
                default:
                    System.out.println("Draw command '" + command + "' not known");
            }
        }
        System.out.println("finished loading input");
    }

    private static void addGeneralLine(Scanner scanner, Model model) {
        final double a = scanner.nextDouble();
        final double b = scanner.nextDouble();
        final double c = -scanner.nextDouble();
        Point<Double> first, second;
        final Point<Double> dir = new Point<>(-b * 100, a * 100);
        if (a != 0) {
            final Point<Double> point = new Point<>(0., -c / b);
            first = new Point<>(point.x + dir.x, point.y + dir.y);
            second = new Point<>(point.x - dir.x, point.y - dir.y);
        } else {
            first = new Point<>(-1000., -c / b);
            second = new Point<>(1000., -c / b);
        }
        addShape(new Line(first, second), model);
    }

    private static void addLine(Scanner scanner, Model model) {
        final double a = scanner.nextDouble();
        final double b = scanner.nextDouble();
        final double c = scanner.nextDouble();
        final double d = scanner.nextDouble();
        final Point<Double> origin = new Point<>(a, b);
        final Point<Double> vector = new Point<>(c, d);
        final Point<Double> first = new Point<>(origin.x - 10 * vector.y, origin.y + 10 * vector.x);
        final Point<Double> second = new Point<>(origin.x + 10 * vector.y, origin.y - 10 * vector.x);
        addShape(new Line(first, second), model);
    }

    private static void addPolygonHalfPlane(Scanner scanner, Model model) {
        final int count = scanner.nextInt();
        final List<HalfPlane> planes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double x = scanner.nextDouble();
            double y = scanner.nextDouble();
            double b = scanner.nextDouble();
            planes.add(new HalfPlane(x, y, b));
        }
        final Polygon polygon = new Polygon();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < i; j++) {
                final Point<Double> point = planes.get(i).intersect(planes.get(j));
                boolean flag = true;
                for (int k = 0; k < count; k++) {
                    if (!planes.get(k).check(point)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    polygon.addPoint(point);
                }
            }
        }
        addShape(polygon, model);
    }

    private static void addEllipse(Scanner scanner, Model model) {
        final Double x = scanner.nextDouble();
        final Double y = scanner.nextDouble();
        final Point<Double> position = new Point<>(x, y);
        final Double rotation = scanner.nextDouble();
        final Double width = scanner.nextDouble();
        final Double height = scanner.nextDouble();
        final Ellipse ellipse = new Ellipse(position, rotation, width, height);
        addShape(ellipse, model);
    }

    private static void addRectangle(Scanner scanner, Model model) {
        final int x = scanner.nextInt();
        final int y = scanner.nextInt();
        final int s = scanner.nextInt();
        final Point<Double> position = new Point<>((double) x, (double) y);
        final Double rotation = 0.0;
        final Double size = (double) s;
        final Rectangle shape = new Rectangle(position, rotation, size);
        addShape(shape, model);
    }

    private static void addPolygon(Scanner scanner, Model model) {
        final int count = scanner.nextInt();
        final Polygon polygon = new Polygon();
        for (int i = 0; i < count; i++) {
            final double x = scanner.nextDouble();
            final double y = scanner.nextDouble();
            final Point<Double> point = new Point<>(x, y);
            polygon.addPoint(point);
        }
        addShape(polygon, model);
    }

    private static void addShape(BaseShape shape, Model model) {
        if (background) {
            model.addBackground(shape);
            background = false;
        } else {
            model.addShape(shape);
        }
    }

    static class HalfPlane {
        double x, y, b;

        HalfPlane(double x, double y, double b) {
            this.x = x;
            this.y = y;
            this.b = b;
        }

        boolean check(Point<Double> pt) {
            return x * pt.x + y * pt.y <= b;
        }

        Point<Double> intersect(HalfPlane h) {
            return new Point<>((b * h.y - y * h.b) / (x * h.y - y * h.x), (x * h.b - b * h.x) / (x * h.y - y * h.x));
        }
    }
}
