package com.exam.instrument.repositories;

import com.exam.instrument.constants.InstrumentType;
import com.exam.instrument.models.Instrument;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface InstrumentRepository extends CrudRepository<Instrument, Long> {
    Page<Instrument> findByName(String name, Pageable pageable);

    Page<Instrument> findByNameAndInstrumentType(String name, InstrumentType instrumentType, Pageable pageable);

    Page<Instrument> findByInstrumentType(InstrumentType instrumentType, Pageable pageable);

    Page<Instrument> list(Pageable pageable);

    List<Instrument> findBySiteIdIn(Collection<Long> siteIds);
}
