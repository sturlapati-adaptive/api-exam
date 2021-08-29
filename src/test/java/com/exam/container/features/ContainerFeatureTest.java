package com.exam.container.features;

import com.exam.Fixtures;
import com.exam.container.commands.ContainerBulkCreateCmd;
import com.exam.container.commands.ContainerCmd;
import com.exam.container.models.Container;
import com.exam.container.repositories.ContainerRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class ContainerFeatureTest {

    @Inject ContainerFeature containerFeature;
    @Inject
    ContainerRepository containerRepository;
    private Container container;

    @BeforeEach
    void setUp() {
        Container c = Fixtures.container("container-1");
        container = containerRepository.save(c);
    }
    @Test
    void should_return_empty_container_when_id_doesnt_exist() {
        Optional<Container> actual = containerFeature.findById(-1);
        assertFalse(actual.isPresent());
    }

    @Test
    void should_return_empty_container_when_barcode_doesnt_exist() {
        Optional<Container> actual = containerFeature.findByBarcode(UUID.randomUUID());
        assertFalse(actual.isPresent());
    }

    @Test
    void should_return_container_by_id_without_instrument() {
        Optional<Container> actual = containerFeature.findById(container.id());
        assertTrue(actual.isPresent());
        assertNotNull(actual.get().id());
        assertEquals(container.id(), actual.get().id());
        assertNotNull(actual.get().description());
        assertEquals("container-1", actual.get().description());
        assertNotNull(actual.get().barcode());
        assertNull(actual.get().instrumentId());
    }

    @Test
    void should_return_container_by_barcode() {
        Optional<Container> actual = containerFeature.findByBarcode(container.barcode());
        assertTrue(actual.isPresent());
        assertNotNull(actual.get().id());
        assertEquals(container.id(), actual.get().id());
        assertNotNull(actual.get().description());
        assertEquals("container-1", actual.get().description());
        assertNotNull(actual.get().barcode());
        assertEquals(container.barcode(), actual.get().barcode());
        assertNull(actual.get().instrumentId());
    }

    @Test
    void should_create_bulk_without_instrument() {
        ContainerBulkCreateCmd bulkCreateCmd = new ContainerBulkCreateCmd(null,
                List.of(new ContainerCmd(UUID.randomUUID(), "container-1")));
        List<Container> actuals = containerFeature.createBulk(List.of(bulkCreateCmd));
        assertNotNull(actuals);
        assertEquals(1, actuals.size());
        Container actual = actuals.get(0);
        assertNotNull(actual.id());
        assertNotNull(actual.barcode());
        assertEquals("container-1", actual.description());
        assertNull(actual.instrumentId());

    }
}
