package com.eci.blueprints.rt.services;



import java.util.List;

import org.springframework.stereotype.Service;

import com.eci.blueprints.rt.filters.BlueprintsFilter;
import com.eci.blueprints.rt.model.Blueprint;
import com.eci.blueprints.rt.model.Point;
import com.eci.blueprints.rt.persistence.BlueprintPersistence;
import com.eci.blueprints.rt.persistence.exception.BlueprintNotFoundException;
import com.eci.blueprints.rt.persistence.exception.BlueprintPersistenceException;

import java.util.Set;

@Service
public class BlueprintsServices {

    private final BlueprintPersistence persistence;
    private final BlueprintsFilter filter;

    public BlueprintsServices(BlueprintPersistence persistence, BlueprintsFilter filter) {
        this.persistence = persistence;
        this.filter = filter;
    }

    public void addNewBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        persistence.saveBlueprint(bp);
    }

    public Set<Blueprint> getAllBlueprints() {
        return persistence.getAllBlueprints();
    }

    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        return persistence.getBlueprintsByAuthor(author);
    }

    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        return filter.apply(persistence.getBlueprint(author, name));
    }

    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        persistence.addPoint(author, name, x, y);
    }

    public void updateBlueprint(String author, String name, List<Point> points) throws BlueprintNotFoundException {
        persistence.updateBlueprint(author, name, points);
    }

    public void deleteBlueprint(String author, String name) throws BlueprintNotFoundException {
        persistence.deleteBlueprint(author, name);
    }
}
