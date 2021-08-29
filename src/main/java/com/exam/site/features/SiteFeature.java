package com.exam.site.features;

import com.exam.common.commands.PageableCommand;
import com.exam.container.features.ContainerFeature;
import com.exam.container.models.Container;
import com.exam.instrument.commands.InstrumentBulkCreateCmd;
import com.exam.instrument.features.InstrumentFeature;
import com.exam.instrument.models.Instrument;
import com.exam.site.commands.SiteCreateCmd;
import com.exam.site.dtos.SiteSearch;
import com.exam.site.models.Site;
import com.exam.site.repositories.SiteRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class SiteFeature {
    private final SiteRepository siteRepository;
    private final InstrumentFeature instrumentFeature;
    private final ContainerFeature containerFeature;

    public SiteFeature(SiteRepository siteRepository,
                       InstrumentFeature instrumentFeature,
                       ContainerFeature containerFeature) {
        this.siteRepository = siteRepository;
        this.instrumentFeature = instrumentFeature;
        this.containerFeature = containerFeature;
    }

    public Optional<Site> findById(long id) {

        return siteRepository.findById(id)
                .map(this::withInstruments);
    }

    private Site withInstruments(Site site) {
        assert site.id() != null;
        Map<Long, List<Instrument>> instrumentsBySiteId = instrumentFeature.findBySiteIdIn(List.of(site.id()));
        List<Instrument> siteInstruments = instrumentsBySiteId.getOrDefault(site.id(), Collections.emptyList());
        Map<Long, List<Container>> containersByInstrumentId = instrumentFeature.fetchContainers(siteInstruments);
        List<Instrument> instruments = instrumentsBySiteId.getOrDefault(site.id(), Collections.emptyList()).stream()
                .map(i -> instrumentFeature.populateContainers(containersByInstrumentId, i))
                .toList();
        return site.withInstruments(instruments);
    }

    public Page<Site> search(@Nullable SiteSearch search, PageableCommand pageableCommand) {
        Pageable pageParams = pageableCommand.page();
        Page<Site> page;
        if (search == null) {
            page = siteRepository.list(pageParams);
        } else if (search.name() != null) {
            page = siteRepository.findByName(search.name(), pageParams);
        } else {
            page = Page.empty();
        }
        return withInstruments(page);
    }

    private Page<Site> withInstruments(Page<Site> page) {
        if(page.isEmpty()){
            return page;
        }
        Set<Long> siteIds = page.getContent().stream()
                .map(Site::id)
                .collect(Collectors.toSet());
        Map<Long, List<Instrument>> instrumentsBySiteId = instrumentFeature.findBySiteIdIn(siteIds);
        List<Instrument> siteInstruments = instrumentsBySiteId.values().stream()
                .flatMap(Collection::stream)
                .toList();
        Map<Long, List<Container>> containersByInstrumentId = instrumentFeature.fetchContainers(siteInstruments);
        return page
                .map(site -> {
                    List<Instrument> instruments = instrumentsBySiteId.getOrDefault(site.id(), Collections.emptyList()).stream()
                            .map(i -> instrumentFeature.populateContainers(containersByInstrumentId, i))
                            .toList();
                    return site.withInstruments(instruments);
                });
    }

    @Transactional
    public List<Site> createBulk(@NonNull List<SiteCreateCmd> cmds) {
        List<Site> created = new ArrayList<>();
        List<InstrumentBulkCreateCmd> instrumentBulkCreateCmds = new ArrayList<>();
        for (SiteCreateCmd cmd : cmds) {
            Site site = siteRepository.save(from(cmd));
            if (cmd.instruments() != null) {
                InstrumentBulkCreateCmd instrumentBulkCreateCmd = new InstrumentBulkCreateCmd(site, cmd.instruments());
                instrumentBulkCreateCmds.add(instrumentBulkCreateCmd);
            }
            created.add(site);
        }
        if (!instrumentBulkCreateCmds.isEmpty()) {
            instrumentFeature.createBulk(instrumentBulkCreateCmds);
        }
        return created;
    }

    static Site from(SiteCreateCmd cmd) {
        return new Site(null, cmd.name(), cmd.shippingAddress(), null, null, null);
    }
}
