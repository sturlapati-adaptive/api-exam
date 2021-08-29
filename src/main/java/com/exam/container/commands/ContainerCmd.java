package com.exam.container.commands;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import java.util.UUID;

public record ContainerCmd(
        @NonNull UUID barcode,
        @Nullable String description) {
}
