package com.exam.site.controllers;

import com.exam.common.commands.PageableCommand;
import com.exam.site.commands.SiteCreateCmd;
import com.exam.site.dtos.SiteSearch;
import com.exam.site.features.SiteFeature;
import com.exam.site.models.PageableSite;
import com.exam.site.models.Site;
import io.micronaut.data.model.Page;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller("/sites")
@Tag(name = "Site")
public class SiteController {
    private final SiteFeature siteFeature;
    public SiteController(SiteFeature siteFeature) {
        this.siteFeature = siteFeature;
    }

    @Get("/{id}")
    @Operation(description = "Find a single site by its Id")
    @ApiResponse(description = "Site")
    public Site findById(Long id) {
        return siteFeature.findById(id).orElse(null);
    }


    @Get("/{?pageable*,search*}")
    @Operation(description = "Returns a paged list of site resource",
            parameters = {
                    @Parameter(name = "name", in = ParameterIn.QUERY),
                    @Parameter(name = "offset", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
                    @Parameter(name = "count", in = ParameterIn.QUERY, schema = @Schema(type = "integer"))
            }
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = PageableSite.class)))
    // Need to hide these parameters for OpenApi doc purposes as we are defining them explicitly. the default generation
    // doesn't seem to handle this well.
    public Page<Site> search(@Parameter(hidden = true) Optional<SiteSearch> search,
                             @Parameter(hidden = true) Optional<PageableCommand> pageable) {
        return siteFeature.search(search.orElse(null), pageable.orElseGet(PageableCommand::defaultPaged));
    }

    @Post("/seed")
    @Operation(description = "Use this to seed data")
    public List<Site> create(@Body List<@Valid SiteCreateCmd> cmds) {
        return siteFeature.createBulk(cmds);
    }
}
