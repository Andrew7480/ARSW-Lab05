package com.eci.blueprints.rt.persistence.postgresql.mapper;

import com.eci.blueprints.rt.model.Blueprint;
import com.eci.blueprints.rt.model.Point;
import com.eci.blueprints.rt.persistence.postgresql.entity.BlueprintEntity;
import com.eci.blueprints.rt.persistence.postgresql.entity.PointEmbeddable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-23T01:19:03-0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class BlueprintMapperImpl implements BlueprintMapper {

    @Override
    public Blueprint toDomain(BlueprintEntity entity) {
        if ( entity == null ) {
            return null;
        }

        List<Point> points = null;
        String author = null;
        String name = null;

        points = pointEmbeddableListToPointList( entity.getPoints() );
        author = entity.getAuthor();
        name = entity.getName();

        Blueprint blueprint = new Blueprint( author, name, points );

        return blueprint;
    }

    @Override
    public BlueprintEntity toEntity(Blueprint blueprint) {
        if ( blueprint == null ) {
            return null;
        }

        BlueprintEntity blueprintEntity = new BlueprintEntity();

        blueprintEntity.setAuthor( blueprint.getAuthor() );
        blueprintEntity.setName( blueprint.getName() );
        blueprintEntity.setPoints( pointListToPointEmbeddableList( blueprint.getPoints() ) );

        return blueprintEntity;
    }

    @Override
    public Point toDomain(PointEmbeddable point) {
        if ( point == null ) {
            return null;
        }

        int x = 0;
        int y = 0;

        x = point.getX();
        y = point.getY();

        Point point1 = new Point( x, y );

        return point1;
    }

    @Override
    public PointEmbeddable toEntity(Point point) {
        if ( point == null ) {
            return null;
        }

        PointEmbeddable pointEmbeddable = new PointEmbeddable();

        pointEmbeddable.setX( point.x() );
        pointEmbeddable.setY( point.y() );

        return pointEmbeddable;
    }

    protected List<Point> pointEmbeddableListToPointList(List<PointEmbeddable> list) {
        if ( list == null ) {
            return null;
        }

        List<Point> list1 = new ArrayList<Point>( list.size() );
        for ( PointEmbeddable pointEmbeddable : list ) {
            list1.add( toDomain( pointEmbeddable ) );
        }

        return list1;
    }

    protected List<PointEmbeddable> pointListToPointEmbeddableList(List<Point> list) {
        if ( list == null ) {
            return null;
        }

        List<PointEmbeddable> list1 = new ArrayList<PointEmbeddable>( list.size() );
        for ( Point point : list ) {
            list1.add( toEntity( point ) );
        }

        return list1;
    }
}
