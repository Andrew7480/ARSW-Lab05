package com.eci.blueprints.rt.dto;

import java.util.List;

import com.eci.blueprints.rt.model.Point;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record NewBlueprintRequest(@NotBlank String author,
        @NotBlank String name,
        @Valid List<Point> points) {

}
