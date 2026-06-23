package com.eci.blueprints.rt.persistence.postgresql.mapper;

import org.mapstruct.Mapper;

import com.eci.blueprints.rt.model.Blueprint;
import com.eci.blueprints.rt.model.Point;
import com.eci.blueprints.rt.persistence.postgresql.entity.BlueprintEntity;
import com.eci.blueprints.rt.persistence.postgresql.entity.PointEmbeddable;



@Mapper(componentModel = "spring")
public interface BlueprintMapper {

    Blueprint toDomain(BlueprintEntity entity);

    BlueprintEntity toEntity(Blueprint blueprint);

    Point toDomain(PointEmbeddable point);

    PointEmbeddable toEntity(Point point);
}