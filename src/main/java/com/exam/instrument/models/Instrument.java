package com.exam.instrument.models;

import com.exam.container.models.Container;
import com.exam.instrument.constants.InstrumentType;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.*;
import io.micronaut.data.model.DataType;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@MappedEntity
public record Instrument(@Id @GeneratedValue @Nullable Long id,
                         @NonNull String name,
                         @TypeDef(type = DataType.STRING) InstrumentType instrumentType,
                         @TypeDef(type = DataType.JSON) @Nullable
                         Map<String, Object> properties,
                         @Nullable Long siteId,
                         @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "instrumentId")
                         @Nullable List<Container> containers,
                         @DateCreated @Nullable Instant created,
                         @DateUpdated @Nullable Instant updated) {

    public Instrument withContainers(List<Container> containers) {
        return new Instrument(
                this.id(),
                this.name(),
                this.instrumentType(),
                this.properties(),
                this.siteId(),
                containers,
                this.created(),
                this.updated()
        );
    }
}
