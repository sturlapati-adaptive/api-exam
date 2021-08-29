package com.exam.instrument.features;

import com.exam.Fixtures;
import com.exam.common.commands.PageableCommand;
import com.exam.instrument.commands.InstrumentBulkCreateCmd;
import com.exam.instrument.commands.InstrumentCmd;
import com.exam.instrument.constants.InstrumentPropertyKey;
import com.exam.instrument.constants.InstrumentType;
import com.exam.instrument.dtos.InstrumentSearch;
import com.exam.instrument.models.Instrument;
import com.exam.instrument.repositories.InstrumentRepository;
import io.micronaut.data.model.Page;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@MicronautTest
class InstrumentFeatureTest {

    @Inject InstrumentFeature instrumentFeature;
    @Inject
    InstrumentRepository instrumentRepository;

    @Test
    void should_get_computer_by_id_without_site() {
        Instrument instrument = Fixtures.computer("computer-1", "mac-address-1");
        Instrument computer = instrumentRepository.save(instrument);
        Optional<Instrument> actual = instrumentFeature.findById(computer.id());
        assertTrue(actual.isPresent());
        assertEquals(computer.id(), actual.get().id());
        assertEquals(computer.name(), actual.get().name());
        assertEquals(InstrumentType.Computer, actual.get().instrumentType());
        assertNotNull(actual.get().properties());
        assertTrue(actual.get().properties().containsKey(InstrumentPropertyKey.MacAddress.name()));
        assertEquals(computer.properties().get(InstrumentPropertyKey.MacAddress.name()), actual.get().properties().get(InstrumentPropertyKey.MacAddress.name()));
        assertNotNull(actual.get().containers());
        assertTrue(actual.get().containers().isEmpty());
        assertNull(actual.get().siteId());
    }

    @Test
    void should_get_freezer_by_id_without_site_and_containers() {
        Instrument instrument = Fixtures.freezer("freezer-1");
        Instrument freezer = instrumentRepository.save(instrument);
        Optional<Instrument> actual = instrumentFeature.findById(freezer.id());
        assertTrue(actual.isPresent());
        assertEquals(freezer.id(), actual.get().id());
        assertEquals(freezer.name(), actual.get().name());
        assertEquals(InstrumentType.Freezer, actual.get().instrumentType());
        assertNull(actual.get().properties());
        assertNotNull(actual.get().containers());
        assertTrue(actual.get().containers().isEmpty());
        assertNull(actual.get().siteId());
    }

    @Test
    void should_find_instrument_by_name() {
        Instrument instrument = Fixtures.computer("computer-2", "mac-address-2");
        Instrument computer = instrumentRepository.save(instrument);
        Page<Instrument> page = instrumentFeature.search(new InstrumentSearch("computer-2", null), PageableCommand.defaultPaged());
        List<Instrument> actuals = page.getContent();
        assertNotNull(actuals);
        assertEquals(1, actuals.size());
        Instrument actual = actuals.get(0);
        assertEquals(computer.id(), actual.id());
    }

    @Test
    void should_find_instrument_by_type() {
        List<Instrument> instruments = List.of(
                Fixtures.computer("computer-3", "mac-address-3"),
                Fixtures.computer("computer-4", "mac-address-4")
        );
        instrumentRepository.saveAll(instruments);
        Page<Instrument> page = instrumentFeature.search(new InstrumentSearch(null, InstrumentType.Computer), PageableCommand.defaultPaged());
        List<Instrument> actuals = page.getContent();
        assertNotNull(actuals);
        assertTrue(actuals.size() >= 2);
        Instrument actual = actuals.get(0);
    }

    @Test
    void should_find_instrument_by_name_and_type() {
        List<Instrument> instruments = List.of(
                Fixtures.computer("computer-5", "mac-address-5"),
                Fixtures.computer("computer-6", "mac-address-6")
        );
        instrumentRepository.saveAll(instruments);
        Page<Instrument> page = instrumentFeature.search(new InstrumentSearch("computer-5", InstrumentType.Computer), PageableCommand.defaultPaged());
        List<Instrument> actuals = page.getContent();
        assertNotNull(actuals);
        assertEquals(1, actuals.size());
        Instrument actual = actuals.get(0);
        assertEquals("computer-5", actual.name());
    }

    @Test
    void should_return_all_instruments() {
        List<Instrument> instruments = List.of(
                Fixtures.computer("computer-7", "mac-address-7"),
                Fixtures.computer("computer-8", "mac-address-8")
        );
        instrumentRepository.saveAll(instruments);
        Page<Instrument> page = instrumentFeature.search(null, PageableCommand.defaultPaged());
        List<Instrument> actuals = page.getContent();
        assertNotNull(actuals);
        assertTrue(actuals.size()>=2);
    }

    @Test
    void should_bulk_create() {
        InstrumentBulkCreateCmd cmd = new InstrumentBulkCreateCmd(null,
                List.of(new InstrumentCmd("bulk-1", InstrumentType.Computer, "mac1", null))
        );
        List<Instrument> actuals = instrumentFeature.createBulk(List.of(cmd));
        assertEquals(1, actuals.size());
    }
}
