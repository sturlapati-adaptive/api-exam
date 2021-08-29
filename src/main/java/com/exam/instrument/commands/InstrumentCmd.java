package com.exam.instrument.commands;

import com.exam.container.commands.ContainerCmd;
import com.exam.instrument.constants.InstrumentType;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import javax.validation.Valid;
import java.util.List;

public record InstrumentCmd(
        @NonNull String name,
        @NonNull InstrumentType instrumentType,
        @Nullable String macAddress,
        @Nullable List<@Valid ContainerCmd> containers) {
}
