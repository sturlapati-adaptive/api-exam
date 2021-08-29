package com.exam.common.dto;

import io.micronaut.core.annotation.Nullable;
public record Address(
        String address1,
        @Nullable String address2,
        String city,
        String state,
        String zip,
        String country
) {
}
