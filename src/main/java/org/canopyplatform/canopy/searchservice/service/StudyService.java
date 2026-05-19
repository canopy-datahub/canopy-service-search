package org.canopyplatform.canopy.searchservice.service;

import org.canopyplatform.canopy.searchservice.auth.SearchAccessContext;
import org.canopyplatform.canopy.searchservice.models.SearchQuery;
import jakarta.servlet.http.HttpServletResponse;

public interface StudyService {

    String searchStudies(SearchQuery searchQuery, SearchAccessContext context);

    void convertSearchStringToCSV(HttpServletResponse response, String s);

    String searchAutocomplete(String query);

}
