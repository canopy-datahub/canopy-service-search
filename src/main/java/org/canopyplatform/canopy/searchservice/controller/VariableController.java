package org.canopyplatform.canopy.searchservice.controller;

import org.canopyplatform.canopy.searchservice.auth.SearchAccessContextResolver;
import org.canopyplatform.canopy.searchservice.service.VariableService;
import org.canopyplatform.canopy.searchservice.models.SearchQuery;
import org.canopyplatform.canopy.searchservice.util.RequestValidator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/variables")
public class VariableController {

    private final VariableService variableService;
    private final SearchAccessContextResolver accessContextResolver;

    @Autowired
    public VariableController(VariableService variableService, SearchAccessContextResolver accessContextResolver) {
        this.variableService = variableService;
        this.accessContextResolver = accessContextResolver;
    }

    @Validated
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> searchVariables(@AuthenticationPrincipal Jwt jwt,
                                                  @Valid SearchQuery searchQuery) {
        RequestValidator.validateSearchQuery(searchQuery);
        return new ResponseEntity<>(
                variableService.searchVariables(searchQuery, accessContextResolver.resolve(jwt)),
                HttpStatus.OK
        );
    }

    @GetMapping(value = "/csv", produces = MediaType.APPLICATION_JSON_VALUE)
    public void searchVariablesToCSV(HttpServletResponse response,
                                     @AuthenticationPrincipal Jwt jwt,
                                     SearchQuery searchQuery) {
        RequestValidator.validateSearchQuery(searchQuery);
        // size is set to 7000 to match the requirement from the UI
        searchQuery.setSize(7000);
        String esResult = variableService.searchVariables(searchQuery, accessContextResolver.resolve(jwt));
        variableService.convertSearchStringToCSV(response, esResult);
    }

}
