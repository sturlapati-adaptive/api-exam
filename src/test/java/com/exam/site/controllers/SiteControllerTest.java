package com.exam.site.controllers;

import com.exam.Fixtures;
import com.exam.container.commands.ContainerCmd;
import com.exam.container.models.Container;
import com.exam.container.repositories.ContainerRepository;
import com.exam.instrument.commands.InstrumentCmd;
import com.exam.instrument.constants.InstrumentType;
import com.exam.instrument.models.Instrument;
import com.exam.instrument.repositories.InstrumentRepository;
import com.exam.site.commands.SiteCreateCmd;
import com.exam.site.models.Site;
import com.exam.site.repositories.SiteRepository;
import io.micronaut.core.type.Argument;
import io.micronaut.data.model.Page;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class SiteControllerTest {

    @Inject
    ContainerRepository containerRepository;
    @Inject
    InstrumentRepository instrumentRepository;
    @Inject
    SiteRepository siteRepository;
    @Inject
    @Client("/sites")
    HttpClient client;
    private List<Site> savedSites;

    @BeforeEach
    void setUp() {

        List<Site> sites = List.of(
                Fixtures.site("site-1"),
                Fixtures.site("site-2")
        );
        savedSites = IterableUtils.toList(siteRepository.saveAll(sites));
        List<Instrument> instruments = List.of(
                Fixtures.computer("computer-1", "mac-1", savedSites.get(0).id()),
                Fixtures.computer("computer-2", "mac-2", savedSites.get(0).id()),
                Fixtures.computer("computer-3", "mac-3", savedSites.get(0).id()),
                Fixtures.freezer("freezer-containers-1", savedSites.get(0).id(), null),
                Fixtures.computer("computer-4", "mac-4", savedSites.get(1).id()),
                Fixtures.computer("computer-5", "mac-5", savedSites.get(1).id()),
                Fixtures.freezer("freezer-containers-2", savedSites.get(1).id(), null)
        );
        List<Instrument> existingInstruments = IterableUtils.toList(instrumentRepository.saveAll(instruments));
        Instrument freezer = existingInstruments.stream()
                .filter(e -> e.name().equals("freezer-containers-2"))
                .findFirst()
                .orElse(null);
        assert freezer != null;
        List<Container> containers = List.of(
                Fixtures.container("ins-1", freezer.id()),
                Fixtures.container("ins-2", freezer.id())
        );
        containerRepository.saveAll(containers);
    }

    @Test
    void should_find_site_by_id() {
        Site expected = savedSites.get(0);
        assert expected.id() != null;
        URI uri = UriBuilder.of("").path(expected.id().toString()).build();
        Site actual = client.toBlocking().retrieve(HttpRequest.GET(uri), Site.class);
        assertNotNull(actual);
        assertEquals(expected.id(), actual.id());
        assertNotNull(actual.shippingAddress());
        assertNotNull(actual.instruments());
        assertEquals(4, actual.instruments().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_find_by_name() {
        Site expected = savedSites.get(0);
        assert expected.id() != null;
        URI uri = UriBuilder.of("")
                .queryParam("name", expected.name())
                .build();
        Page<Site> page = client.toBlocking().retrieve(HttpRequest.GET(uri), Argument.of(Page.class, Argument.of(Site.class)));
        assertTrue(page.getContent().size() >= 1);
        Site actual = page.getContent().stream()
                .filter(s -> expected.id().equals(s.id()))
                .findFirst()
                .orElse(null);
        assertNotNull(actual);
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
        List<SiteCreateCmd> cmds = List.of(cmd1, cmd2);
        List<Site> actual = client.toBlocking().retrieve(HttpRequest.POST("/seed", cmds), Argument.listOf(Site.class));
        assertEquals(cmds.size(), actual.size());

    }

    @Test
    void should_error_on_create_bulk_when_there_are_duplicate_container_barcodes() {
        UUID dupeBarcode = UUID.randomUUID();
        SiteCreateCmd cmd1 = new SiteCreateCmd(
                "bulk-site-1",
                Fixtures.address(),
                List.of(
                        new InstrumentCmd("ins-1", InstrumentType.Computer, "mac-1", null),
                        new InstrumentCmd("ins-2", InstrumentType.Freezer, null,
                                List.of(
                                        new ContainerCmd(dupeBarcode, "container-1"),
                                        new ContainerCmd(dupeBarcode, "container-2")
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
        List<SiteCreateCmd> cmds = List.of(cmd1, cmd2);
        HttpClientResponseException actual = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(HttpRequest.POST("/seed", cmds)));
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatus());
        assertTrue(actual.getMessage().contains("Duplicate barcode"));
    }
}
