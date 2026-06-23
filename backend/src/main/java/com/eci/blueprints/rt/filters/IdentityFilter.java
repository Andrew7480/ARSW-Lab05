package com.eci.blueprints.rt.filters;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.eci.blueprints.rt.model.Blueprint;

/**
 * Default filter: returns the blueprint unchanged.
 * This matches the baseline behavior of the reference lab before students implement custom filters.
 */
@Component
@Profile("!redundancy & !undersampling")
public class IdentityFilter implements BlueprintsFilter {
    @Override
    public Blueprint apply(Blueprint bp) { return bp; }
}
