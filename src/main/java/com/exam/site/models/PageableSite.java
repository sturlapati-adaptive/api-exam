package com.exam.site.models;

import java.util.List;

public interface PageableSite  {
    int getTotalPages();
    int getPageNumber();
    long getOffset();
    int getNumberOfElements();
    int getSize();
    List<Site> getContent();
}
