package com.exam.instrument.controllers;

import com.exam.Fixtures;
import com.exam.container.models.Container;
import com.exam.container.repositories.ContainerRepository;
import com.exam.instrument.constants.InstrumentType;
import com.exam.instrument.models.Instrument;
import com.exam.instrument.repositories.InstrumentRepository;
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

import static org.junit.jupiter.api.Assertions.*;
@MicronautTest
class InstrumentControllerTest {
    @Inject
    ContainerRepository containerRepository;
    @Inject
    InstrumentRepository instrumentRepository;
    @Inject
    @Client("/instruments")
    HttpClient client;
    private List<Instrument> existingInstruments;

    @BeforeEach
    void setUp() {

        List<Instrument> instruments = List.of(
                Fixtures.computer("computer-1", "mac-1"),
                Fixtures.computer("computer-2", "mac-2"),
                Fixtures.computer("computer-3", "mac-3"),
                Fixtures.freezer("freezer-containers-1", null, null)
        );
        existingInstruments = IterableUtils.toList(instrumentRepository.saveAll(instruments));
        Instrument freezer = existingInstruments.stream()
                .filter(e->e.name().equals("freezer-containers-1"))
                .findFirst()
                .orElse(null);
        assert freezer != null;
        List<Container> containers = List.of(
                Fixtures.container("ins-1", freezer.id()),
                Fixtures.container("ins-2", freezer.id())
        );
        List<Container> existingContainers = IterableUtils.toList(containerRepository.saveAll(containers));
    }

    @Test
    void should_return_not_found_when_id_doesnt_exist() {
        URI uri = UriBuilder.of("").path("-1").build();
        HttpClientResponseException actual = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().retrieve(HttpRequest.GET(uri), Instrument.class));
        assertNotNull(actual);
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatus());
    }

    @Test
    void should_find_by_id() {
        Instrument expected = existingInstruments.get(0);
        assert expected.id() != null;
        URI uri = UriBuilder.of("").path(expected.id().toString()).build();
        Instrument actual = client.toBlocking().retrieve(HttpRequest.GET(uri), Instrument.class);
        assertNotNull(actual);
        assertEquals(expected.id(), actual.id());
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_find_by_name() {
        URI uri = UriBuilder.of("")
                .queryParam("name", "freezer-containers-1")
                .queryParam("offset", 0)
                .queryParam("count", 1)
                .build();
        Page<Instrument> actuals = client.toBlocking().retrieve(HttpRequest.GET(uri), Argument.of(Page.class, Argument.of(Instrument.class)));
        assertNotNull(actuals);
        assertEquals(1, actuals.getContent().size());
        Instrument actual = actuals.getContent().get(0);
        assertEquals("freezer-containers-1", actual.name());
        assertNotNull(actual.containers());
        assertEquals(2, actual.containers().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_find_by_type_with_paging() {
        URI uri = UriBuilder.of("")
                .queryParam("instrumentType", InstrumentType.Computer)
                .queryParam("offset", 0)
                .queryParam("count", 1)
                .build();
        Page<Instrument> actuals = client.toBlocking().retrieve(HttpRequest.GET(uri), Argument.of(Page.class, Argument.of(Instrument.class)));
        assertNotNull(actuals);
        assertTrue(actuals.getTotalSize()>=3);
        assertEquals(1, actuals.getNumberOfElements());
        assertEquals(InstrumentType.Computer, actuals.getContent().get(0).instrumentType());
    }

}
