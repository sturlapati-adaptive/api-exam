package com.exam.site.repositories;

import com.exam.site.models.Site;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SiteRepository extends CrudRepository<Site, Long> {
    Page<Site> list(Pageable pageParams);

    Page<Site> findByName(String name, Pageable pageParams);
}
