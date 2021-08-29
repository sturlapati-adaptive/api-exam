package com.exam.container.features;

import com.exam.common.commands.PageableCommand;
import com.exam.container.commands.ContainerBulkCreateCmd;
import com.exam.container.commands.ContainerCmd;
import com.exam.container.models.Container;
import com.exam.container.repositories.ContainerRepository;
import com.exam.instrument.models.Instrument;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import jakarta.inject.Singleton;
import org.apache.commons.collections4.IterableUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class ContainerFeature {
    private final ContainerRepository containerRepository;

    public ContainerFeature(ContainerRepository containerRepository) {
        this.containerRepository = containerRepository;
    }

    public Optional<Container> findByBarcode(UUID barcode) {
        List<Container> containers = containerRepository.findByBarcode(barcode);
        if (containers.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(containers.get(0));
    }

    public Optional<Container> findById(long id) {
        return containerRepository.findById(id);
    }

    @Transactional
    public List<Container> createBulk(@NonNull List<ContainerBulkCreateCmd> containerBulkCreateCmds) {
        List<Container> containers = containerBulkCreateCmds.stream()
                .flatMap(bc -> bc.containers().stream().map(c -> from(c, bc.instrument())))
                .toList();
        return IterableUtils.toList(containerRepository.saveAll(containers));
    }

    static Container from(@NonNull ContainerCmd cmd, @Nullable Instrument instrument) {
        return new Container(
                null,
                cmd.barcode(),
                cmd.description(),
                Optional.ofNullable(instrument).map(Instrument::id).orElse(null),
                null,
                null
        );
    }

    public Page<Container> list(PageableCommand pageableCommand) {
        return containerRepository.list(pageableCommand.page());
    }

    public InstrumentContainers getInstrumentContainers(List<Instrument> instruments) {
        return new InstrumentContainers(containerRepository).of(instruments);
    }

    public static class InstrumentContainers {
        private Map<Long, List<Container>> containersByInstrumentId;
        private final ContainerRepository containerRepository;

        private InstrumentContainers(ContainerRepository containerRepository) {
            this.containerRepository = containerRepository;
            this.containersByInstrumentId = Collections.emptyMap();
        }

        private InstrumentContainers of(@NonNull List<Instrument> instruments) {
            if (instruments.isEmpty()) {
                return this;
            }
            fetchContainers(instruments);
            return this;
        }

        private Map<Long, List<Container>> findByInstrumentIdIn(Collection<Long> instrumentIds) {
            return containerRepository.findByInstrumentIdIn(instrumentIds).stream()
                    .filter(c -> c.instrumentId() != null)
                    .collect(Collectors.groupingBy(Container::instrumentId));
        }

        private void fetchContainers(List<Instrument> instruments) {
            Set<Long> ids = instruments.stream()
                    .map(Instrument::id)
                    .collect(Collectors.toSet());
            this.containersByInstrumentId = findByInstrumentIdIn(ids);
        }

        public Instrument map(Instrument i) {
            List<Container> containers = containersByInstrumentId.getOrDefault(i.id(), Collections.emptyList());
            return i.withContainers(containers);
        }
    }
}
