package com.exam.instrument.commands;

import com.exam.site.models.Site;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import java.util.List;

public record InstrumentBulkCreateCmd(
        @Nullable Site site,
        @NonNull List<InstrumentCmd> instruments
) {
}
