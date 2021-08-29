package com.exam.instrument.dtos;

import com.exam.instrument.constants.InstrumentType;
import io.micronaut.core.annotation.Nullable;

public record InstrumentSearch(@Nullable String name,
                               @Nullable InstrumentType instrumentType) {
}
