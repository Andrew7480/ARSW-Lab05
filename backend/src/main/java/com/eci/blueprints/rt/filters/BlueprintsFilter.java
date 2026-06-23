package com.eci.blueprints.rt.filters;

import com.eci.blueprints.rt.model.Blueprint;

public interface BlueprintsFilter {
    Blueprint apply(Blueprint bp);
}
