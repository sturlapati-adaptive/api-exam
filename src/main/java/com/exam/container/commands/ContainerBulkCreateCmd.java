package com.exam.container.commands;

import com.exam.instrument.models.Instrument;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import java.util.List;

public record ContainerBulkCreateCmd(
        @Nullable Instrument instrument,
        @NonNull List<ContainerCmd> containers
) {
}
