package com.eci.blueprints.rt.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.eci.blueprints.rt.dto.*;
import com.eci.blueprints.rt.model.Blueprint;
import com.eci.blueprints.rt.model.Point;
import com.eci.blueprints.rt.persistence.exception.BlueprintNotFoundException;
import com.eci.blueprints.rt.persistence.exception.BlueprintPersistenceException;
import com.eci.blueprints.rt.services.BlueprintsServices;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/blueprints")
public class BlueprintController {

  private final SimpMessagingTemplate template;
  private final BlueprintsServices services;

  public BlueprintController(SimpMessagingTemplate template, BlueprintsServices services) {
    this.template = template;
    this.services = services;
  }

  // ── WebSocket / STOMP ─────────────────────────────────────────────────────

  @MessageMapping("/draw")
  public void onDraw(DrawEvent evt) {
    try {
      services.addPoint(evt.author(), evt.name(), evt.point().x(), evt.point().y());
    } catch (BlueprintNotFoundException e) {
      return;
    }
    template.convertAndSend(
        "/topic/blueprints." + evt.author() + "." + evt.name(),
        new BlueprintUpdate(evt.author(), evt.name(), List.of(evt.point())));
  }

  // ── REST CRUD ─────────────────────────────────────────────────────────────

  @Operation(summary = "Get all blueprints")
  @GetMapping
  public ResponseEntity<ApiResponse<Set<Blueprint>>> getAll() {
    return ResponseEntity.ok(ApiResponse.ok(services.getAllBlueprints()));
  }

  @Operation(summary = "Get all blueprints by author")
  @GetMapping("/{author}")
  public ResponseEntity<ApiResponse<Set<Blueprint>>> byAuthor(@PathVariable String author)
      throws BlueprintNotFoundException {
    return ResponseEntity.ok(ApiResponse.ok(services.getBlueprintsByAuthor(author)));
  }

  @Operation(summary = "Get a specific blueprint")
  @GetMapping("/{author}/{name}")
  public ResponseEntity<ApiResponse<Blueprint>> byAuthorAndName(
      @PathVariable String author, @PathVariable String name)
      throws BlueprintNotFoundException {
    return ResponseEntity.ok(ApiResponse.ok(services.getBlueprint(author, name)));
  }

  @Operation(summary = "Create a new blueprint")
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> add(@Valid @RequestBody NewBlueprintRequest req)
      throws BlueprintPersistenceException {
    services.addNewBlueprint(new Blueprint(req.author(), req.name(), req.points()));
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(null));
  }

  @Operation(summary = "Replace all points of a blueprint (Save)")
  @PutMapping("/{author}/{name}")
  public ResponseEntity<ApiResponse<Void>> update(
      @PathVariable String author, @PathVariable String name,
      @RequestBody List<Point> points)
      throws BlueprintNotFoundException {
    services.updateBlueprint(author, name, points);
    return ResponseEntity.accepted().body(ApiResponse.accepted());
  }

  @Operation(summary = "Delete a blueprint")
  @DeleteMapping("/{author}/{name}")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable String author, @PathVariable String name)
      throws BlueprintNotFoundException {
    services.deleteBlueprint(author, name);
    return ResponseEntity.accepted().body(ApiResponse.accepted());
  }
}
