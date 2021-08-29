package com.exam.site.features;

import com.exam.common.commands.PageableCommand;
import com.exam.container.features.ContainerFeature;
import com.exam.instrument.commands.InstrumentBulkCreateCmd;
import com.exam.instrument.features.InstrumentFeature;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class SiteFeature {
    private final SiteRepository siteRepository;
    private final InstrumentFeature instrumentFeature;

    public SiteFeature(SiteRepository siteRepository,
                       InstrumentFeature instrumentFeature,
                       ContainerFeature containerFeature) {
        this.siteRepository = siteRepository;
        this.instrumentFeature = instrumentFeature;
    }

    public Optional<Site> findById(long id) {

        return siteRepository.findById(id)
                .map(this::withInstruments);
    }

    private Site withInstruments(Site site) {
        return instrumentFeature.getSiteInstruments(List.of(site)).map(site);
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
        if (page.isEmpty()) {
            return page;
        }
        InstrumentFeature.SiteInstruments si = instrumentFeature.getSiteInstruments(page.getContent());
        return page.map(si::map);
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
