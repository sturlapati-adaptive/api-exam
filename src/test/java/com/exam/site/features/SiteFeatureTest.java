package com.exam.site.features;

import com.exam.Fixtures;
import com.exam.common.commands.PageableCommand;
import com.exam.container.commands.ContainerCmd;
import com.exam.instrument.commands.InstrumentCmd;
import com.exam.instrument.constants.InstrumentType;
import com.exam.site.commands.SiteCreateCmd;
import com.exam.site.dtos.SiteSearch;
import com.exam.site.models.Site;
import com.exam.site.repositories.SiteRepository;
import io.micronaut.data.model.Page;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@MicronautTest
class SiteFeatureTest {
    @Inject SiteFeature siteFeature;
    @Inject
    SiteRepository siteRepository;
    @Test
    void should_find_by_id() {
        Site site = Fixtures.site("site-1");
        Site saved = siteRepository.save(site);
        Optional<Site> actual = siteFeature.findById(saved.id());
        assertTrue(actual.isPresent());
        assertEquals(saved.id(), actual.get().id());
        assertEquals(saved.name(), actual.get().name());
        assertNotNull(actual.get().shippingAddress());
        assertEquals(saved.shippingAddress(), actual.get().shippingAddress());
    }

    @Test
    void should_find_by_name() {
        Site site = Fixtures.site("site-2");
        Site saved = siteRepository.save(site);
        Page<Site> actual = siteFeature.search(new SiteSearch("site-2"), PageableCommand.defaultPaged());
        List<Site> actuals = actual.getContent();
        assertEquals(1, actuals.size());
    }

    @Test
    void should_return_all() {
        List<Site> sites = List.of(
                Fixtures.site("site-3"),
                Fixtures.site("site-4")
        );
        siteRepository.saveAll(sites);
        Page<Site> actual = siteFeature.search(null, PageableCommand.defaultPaged());
        List<Site> actuals = actual.getContent();
        assertTrue(actuals.size()>=2);
    }

    @Test
    void should_create_bulk() {
        SiteCreateCmd cmd1 = new SiteCreateCmd(
                "bulk-site-1",
                Fixtures.address(),
                List.of(
                        new InstrumentCmd("ins-1", InstrumentType.Computer, "mac-1", null),
                        new InstrumentCmd("ins-2", InstrumentType.Freezer, null,
                                List.of(
                                        new ContainerCmd(UUID.randomUUID(), "container-1"),
                                        new ContainerCmd(UUID.randomUUID(), "container-2")
                                )
                        )
                )
        );
        SiteCreateCmd cmd2 = new SiteCreateCmd(
                "bulk-site-2",
                Fixtures.address(),
                List.of(
                        new InstrumentCmd("ins-3", InstrumentType.Computer, "mac-3", null),
                        new InstrumentCmd("ins-4", InstrumentType.Freezer, null,
                                List.of(
                                        new ContainerCmd(UUID.randomUUID(), "container-3"),
                                        new ContainerCmd(UUID.randomUUID(), "container-4")
                                )
                        )
                )
        );
        List<Site> actuals = siteFeature.createBulk(List.of(cmd1, cmd2));
        assertEquals(2, actuals.size());
    }
}
