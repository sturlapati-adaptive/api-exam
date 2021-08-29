package com.exam.container.controllers;

import com.exam.common.commands.PageableCommand;
import com.exam.container.features.ContainerFeature;
import com.exam.container.models.Container;
import com.exam.container.models.PageableContainer;
import io.micronaut.data.model.Page;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;
import java.util.UUID;

@Controller("/containers")
@Tag(name="Container")
public class ContainerController {
    private final ContainerFeature containerFeature;

    public ContainerController(ContainerFeature containerFeature) {
        this.containerFeature = containerFeature;
    }

    @Get("/{?pageable*}")
    @Operation(description = "Returns a pageable list of containers",
            parameters = {
                    @Parameter(name = "offset", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
                    @Parameter(name = "count", in = ParameterIn.QUERY, schema = @Schema(type = "integer"))
            })
    @ApiResponse(content = @Content(schema = @Schema(implementation = PageableContainer.class)))
    // Need to hide these parameters for OpenApi doc purposes as we are defining them explicitly. the default generation
    // doesn't seem to handle this well.
    public Page<Container> list(@Parameter(hidden = true) Optional<PageableCommand> pageable) {
        return containerFeature.list(pageable.orElseGet(PageableCommand::defaultPaged));
    }

    @Get("/{id}")
    @Operation(description = "Find a single container by its Id")
    public Container getById(long id) {
        return containerFeature.findById(id).orElse(null);
    }

    @Get("/barcode/{barcode}")
    @Operation(description = "Find a single container by its barcode")
    public Container getByBarcode(UUID barcode) {
        return containerFeature.findByBarcode(barcode).orElse(null);
    }
}
