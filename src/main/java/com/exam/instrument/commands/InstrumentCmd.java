package com.exam.instrument.commands;

import com.exam.container.commands.ContainerCmd;
import com.exam.instrument.constants.InstrumentType;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import java.util.List;

public record InstrumentCmd(
        @NonNull String name,
        @NonNull InstrumentType instrumentType,
        @Nullable String macAddress,
        @Nullable List<ContainerCmd> containers) {
}
