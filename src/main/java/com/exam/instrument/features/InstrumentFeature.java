package com.exam.instrument.features;

import com.exam.common.commands.PageableCommand;
import com.exam.container.commands.ContainerBulkCreateCmd;
import com.exam.container.features.ContainerFeature;
import com.exam.container.models.Container;
import com.exam.instrument.commands.InstrumentBulkCreateCmd;
import com.exam.instrument.commands.InstrumentCmd;
import com.exam.instrument.constants.InstrumentPropertyKey;
import com.exam.instrument.dtos.InstrumentSearch;
import com.exam.instrument.models.Instrument;
import com.exam.instrument.repositories.InstrumentRepository;
import com.exam.site.models.Site;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class InstrumentFeature {
    private final InstrumentRepository instrumentRepository;
    private final ContainerFeature containerFeature;

    public InstrumentFeature(InstrumentRepository instrumentRepository,
                             ContainerFeature containerFeature) {
        this.instrumentRepository = instrumentRepository;
        this.containerFeature = containerFeature;
    }

    public Optional<Instrument> findById(long id) {

        return instrumentRepository.findById(id)
                .map(this::withContainers);
    }

    private Instrument withContainers(Instrument instrument) {
        Map<Long, List<Container>> containersByInstrument = fetchContainers(List.of(instrument));
        List<Container> containers = containersByInstrument.getOrDefault(instrument.id(), Collections.emptyList());
        return instrument.withContainers(containers);
    }

    public Page<Instrument> search(@Nullable InstrumentSearch search, PageableCommand pageable) {
        Pageable pageParams = pageable.page();
        Page<Instrument> instruments;
        if (search == null) {
            instruments = instrumentRepository.list(pageParams);
        }
        else if (search.name() != null && search.instrumentType() != null) {
            instruments =instrumentRepository.findByNameAndInstrumentType(search.name(), search.instrumentType(), pageParams);
        }
        else if (search.name() != null) {
            instruments = instrumentRepository.findByName(search.name(), pageParams);
        }
        else if (search.instrumentType() != null) {
            instruments = instrumentRepository.findByInstrumentType(search.instrumentType(), pageParams);
        }
        else {
            instruments = Page.empty();
        }
        return withContainers(instruments);
    }

    private Page<Instrument> withContainers(Page<Instrument> instruments) {
        if(instruments.isEmpty()){
            return instruments;
        }
        Map<Long, List<Container>> containersByInstrument = fetchContainers(instruments.getContent());
        return instruments
                .map(i -> populateContainers(containersByInstrument, i));
    }

    public Instrument populateContainers(Map<Long, List<Container>> containersByInstrument, Instrument i) {
        List<Container> containers = containersByInstrument.getOrDefault(i.id(), Collections.emptyList());
        return i.withContainers(containers);
    }

    public Map<Long, List<Container>> fetchContainers(List<Instrument> instruments) {
        Set<Long> ids = instruments.stream()
                .map(Instrument::id)
                .collect(Collectors.toSet());
        return containerFeature.findByInstrumentIdIn(ids);
    }

    @Transactional
    public List<Instrument> createBulk(@NonNull List<InstrumentBulkCreateCmd> instrumentBulkCreateCmds) {
        List<ContainerBulkCreateCmd> containerBulkCreateCmds = new ArrayList<>();
        List<Instrument> created = new ArrayList<>();
        for(InstrumentBulkCreateCmd bulkCreateCmd: instrumentBulkCreateCmds) {
            for(InstrumentCmd instrumentCmd: bulkCreateCmd.instruments()){
                Instrument instrument = instrumentRepository.save(from(instrumentCmd, bulkCreateCmd.site()));
                if (instrumentCmd.containers() != null) {
                    ContainerBulkCreateCmd cmd = new ContainerBulkCreateCmd(instrument, instrumentCmd.containers());
                    containerBulkCreateCmds.add(cmd);
                }
                created.add(instrument);
            }
        }
        if(!containerBulkCreateCmds.isEmpty()){
            containerFeature.createBulk(containerBulkCreateCmds);
        }
        return created;
    }

    static Instrument from(InstrumentCmd cmd, @Nullable Site site) {
        return new Instrument(
                null,
                cmd.name(),
                cmd.instrumentType(),
                properties(cmd),
                Optional.ofNullable(site).map(Site::id).orElse(null),
                null,
                null,
                null
        );
    }

    static Map<String, Object> properties(InstrumentCmd cmd) {
        Map<String, Object> props = new HashMap<>();
        if (cmd.macAddress() != null) {
            props.put(InstrumentPropertyKey.MacAddress.name(), cmd.macAddress());
        }
        return props;
    }

    public Map<Long, List<Instrument>> findBySiteIdIn(Collection<Long> siteIds) {
        return instrumentRepository.findBySiteIdIn(siteIds).stream()
                .filter(i->i.siteId()!=null)
                .collect(Collectors.groupingBy(Instrument::siteId));
    }
}
