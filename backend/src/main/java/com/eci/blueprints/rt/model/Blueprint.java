package com.eci.blueprints.rt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Blueprint {

    private String author;
    private String name;
    private final List<Point> points = new ArrayList<>();

    public Blueprint(String author, String name, List<Point> points) {
        this.author = author;
        this.name = name;
        if (points != null) this.points.addAll(points);
    }

    public String getAuthor() { return author; }
    public String getName() { return name; }
    public List<Point> getPoints() { return Collections.unmodifiableList(points); }

    public void addPoint(Point p) { points.add(p); }

    public void replacePoints(List<Point> newPoints) {
        points.clear();
        if (newPoints != null) points.addAll(newPoints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Blueprint bp)) return false;
        return Objects.equals(author, bp.author) && Objects.equals(name, bp.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, name);
    }
}
