package com.eci.blueprints.rt.persistence;

import java.util.List;
import java.util.Set;

import com.eci.blueprints.rt.model.Blueprint;
import com.eci.blueprints.rt.model.Point;
import com.eci.blueprints.rt.persistence.exception.BlueprintNotFoundException;
import com.eci.blueprints.rt.persistence.exception.BlueprintPersistenceException;

public interface BlueprintPersistence {

    void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException;

    Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException;

    Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException;

    Set<Blueprint> getAllBlueprints();

    void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException;

    void updateBlueprint(String author, String name, List<Point> points) throws BlueprintNotFoundException;

    void deleteBlueprint(String author, String name) throws BlueprintNotFoundException;
}
