package com.exam.container.repositories;

import com.exam.container.models.Container;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ContainerRepository extends CrudRepository<Container, Long> {
    List<Container> findByBarcode(UUID barcode);

    Page<Container> list(Pageable page);

    List<Container> findByInstrumentIdIn(Collection<Long> instrumentIds);
}
