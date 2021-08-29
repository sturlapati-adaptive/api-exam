package com.exam.common.commands;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Pageable;

import static com.exam.common.constants.Common.DEFAULT_PAGE_SIZE;

public record PageableCommand(
        @Nullable Integer offset,
        @Nullable Integer count
) {
    public Pageable page(){
        return offset == null || count == null ? Pageable.from(0, DEFAULT_PAGE_SIZE) : Pageable.from(offset, count);
    }
    public static PageableCommand defaultPaged(){
        return new PageableCommand(0, DEFAULT_PAGE_SIZE);
    }
}
