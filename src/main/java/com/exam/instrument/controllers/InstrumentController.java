package com.exam.instrument.controllers;

import com.exam.common.commands.PageableCommand;
import com.exam.instrument.dtos.InstrumentSearch;
import com.exam.instrument.features.InstrumentFeature;
import com.exam.instrument.models.Instrument;
import com.exam.instrument.models.PageableInstrument;
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

@Controller("/instruments")
@Tag(name = "Instrument")
public class InstrumentController {
    private final InstrumentFeature instrumentFeature;

    public InstrumentController(InstrumentFeature instrumentFeature) {
        this.instrumentFeature = instrumentFeature;
    }

    @Get("/{id}")
    @Operation(description = "Find a single instrument by its Id")
    public Instrument getById(long id) {
        return instrumentFeature.findById(id).orElse(null);
    }

    @Get("/{?pageable*,search*}")
    @Operation(description = "Returns a paged list of instruments",
            parameters = {
                    @Parameter(name = "name", in = ParameterIn.QUERY),
                    @Parameter(name = "instrumentType", in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", allowableValues = {"Computer", "Freezer"})),
                    @Parameter(name = "offset", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
                    @Parameter(name = "count", in = ParameterIn.QUERY, schema = @Schema(type = "integer"))
            }
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = PageableInstrument.class)))
    // Need to hide these parameters for OpenApi doc purposes as we are defining them explicitly. the default generation
    // doesn't seem to handle this well.
    public Page<Instrument> find(@Parameter(hidden = true) Optional<InstrumentSearch> search,
                                 @Parameter(hidden = true) Optional<PageableCommand> pageable) {
        return instrumentFeature.search(search.orElse(null), pageable.orElseGet(PageableCommand::defaultPaged));
    }
}
