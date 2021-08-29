package com.exam.container.models;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.*;

import java.time.Instant;
import java.util.UUID;

@MappedEntity
public record Container(
        @Id @GeneratedValue @Nullable Long id,
        @NonNull UUID barcode,
        @Nullable String description,
        @Nullable Long instrumentId,
        @DateCreated @Nullable Instant created,
        @DateUpdated @Nullable Instant updated) {
}
