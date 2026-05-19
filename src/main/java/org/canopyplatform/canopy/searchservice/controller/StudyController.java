package org.canopyplatform.canopy.searchservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.canopyplatform.canopy.searchservice.auth.SearchAccessContextResolver;
import org.canopyplatform.canopy.searchservice.service.StudyService;
import org.canopyplatform.canopy.searchservice.models.FacetDTO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/studies")
public class StudyController {

    private final StudyService studyService;
    private final SearchAccessContextResolver accessContextResolver;

    private final TypeReference<List<FacetDTO>> typeReference;
    private final ObjectMapper mapper;

    @Autowired
    public StudyController(StudyService studyService, SearchAccessContextResolver accessContextResolver) {
        this.studyService = studyService;
        this.accessContextResolver = accessContextResolver;
        typeReference = new TypeReference<List<FacetDTO>>() {};
        mapper = new ObjectMapper();
    }

    @Validated
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> searchStudies(@AuthenticationPrincipal Jwt jwt,
                                                @Valid SearchQuery searchQuery) {
        RequestValidator.validateSearchQuery(searchQuery);
        return new ResponseEntity<>(
                studyService.searchStudies(searchQuery, accessContextResolver.resolve(jwt)),
                HttpStatus.OK
        );
    }

    @GetMapping(value = "/csv", produces = MediaType.APPLICATION_JSON_VALUE)
    public void searchStudiesToCSV(HttpServletResponse response,
                                   @AuthenticationPrincipal Jwt jwt,
                                   SearchQuery searchQuery) {
        RequestValidator.validateSearchQuery(searchQuery);
        // size is hard-coded to 999 to show all results by default
        searchQuery.setSize(999);
        String esResult = studyService.searchStudies(searchQuery, accessContextResolver.resolve(jwt));
        studyService.convertSearchStringToCSV(response, esResult);
    }

    @GetMapping(value = "/autocomplete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> searchAutocomplete(@RequestParam(defaultValue = "") String q) {
        RequestValidator.validateStringRequestParams(List.of(q));
        return ResponseEntity.ok(studyService.searchAutocomplete(q));
    }

}
