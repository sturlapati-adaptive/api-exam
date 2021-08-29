package com.exam.site.models;

import com.exam.common.dto.Address;
import com.exam.instrument.models.Instrument;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.*;
import io.micronaut.data.model.DataType;

import java.time.Instant;
import java.util.List;
@MappedEntity
public record Site(@Id @GeneratedValue @Nullable Long id,
                   String name,
                   @TypeDef(type= DataType.JSON)
                   Address shippingAddress,
                   @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "site") @Nullable
                   List<Instrument> instruments,
                   @DateCreated @Nullable Instant created,
                   @DateUpdated @Nullable Instant updated
                   ) {

    public Site withInstruments(List<Instrument> instruments){
        return new Site(this.id(), this.name(), this.shippingAddress(), instruments, this.created(), this.updated());
    }
}
