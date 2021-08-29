package com.exam.container.models;

import java.util.List;

public interface PageableContainer {
    int getTotalPages();
    int getPageNumber();
    long getOffset();
    int getNumberOfElements();
    int getSize();
    List<Container> getContent();
}
