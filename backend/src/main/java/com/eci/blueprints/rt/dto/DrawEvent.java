package com.eci.blueprints.rt.dto;

import com.eci.blueprints.rt.model.Point;

public record DrawEvent(String author, String name, Point point) {}
