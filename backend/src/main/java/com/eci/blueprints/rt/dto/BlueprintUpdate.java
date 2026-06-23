package com.eci.blueprints.rt.dto;

import java.util.List;

import com.eci.blueprints.rt.model.Point;

public record BlueprintUpdate(String author, String name, List<Point> points) {}
