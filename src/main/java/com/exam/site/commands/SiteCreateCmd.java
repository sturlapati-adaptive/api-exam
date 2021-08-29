package com.exam.site.commands;

import com.exam.common.dto.Address;
import com.exam.instrument.commands.InstrumentCmd;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import javax.validation.Valid;
import java.util.List;
public record SiteCreateCmd(
        @NonNull String name,
        @NonNull Address shippingAddress,
        @Nullable List<@Valid InstrumentCmd> instruments){
}
