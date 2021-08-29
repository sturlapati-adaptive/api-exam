package com.exam.container.controllers;

import com.exam.Fixtures;
import com.exam.container.models.Container;
import com.exam.container.repositories.ContainerRepository;
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
class ContainerControllerTest {
    @Inject
    ContainerRepository containerRepository;
    @Inject
    @Client("/containers")
    HttpClient client;
    private List<Container> existing;

    @BeforeEach
    void setUp() {
        List<Container> containers = List.of(
                Fixtures.container("ins-1"),
                Fixtures.container("ins-2")
        );
        existing = IterableUtils.toList(containerRepository.saveAll(containers));
    }

    @Test
    void should_return_not_found_when_id_doesnt_exist() {
        URI uri = UriBuilder.of("").path("-1").build();
        HttpClientResponseException actual = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().retrieve(HttpRequest.GET(uri), Container.class));
        assertNotNull(actual);
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatus());
    }

    @Test
    void should_find_by_id() {
        Container expected = existing.get(0);
        URI uri = UriBuilder.of("").path(expected.id().toString()).build();
        Container actual = client.toBlocking().retrieve(HttpRequest.GET(uri), Container.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void should_find_by_barcode() {
        Container expected = existing.get(1);
        URI uri = UriBuilder.of("barcode").path(expected.barcode().toString()).build();
        Container actual = client.toBlocking().retrieve(HttpRequest.GET(uri), Container.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void should_return_paged_containers() {
        Container expected = existing.get(1);
        URI uri = UriBuilder.of("")
                .queryParam("offset", 0)
                .queryParam("count", 1)
                .build();
        Page<Container> actual = client.toBlocking().retrieve(HttpRequest.GET(uri), Argument.of(Page.class, Argument.of(Container.class)));
        assertNotNull(actual);
        assertEquals(1, actual.getNumberOfElements());
        assertTrue(actual.getTotalSize()>=2);
    }
}
