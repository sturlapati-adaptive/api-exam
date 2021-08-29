package com.exam.instrument.models;

import java.util.List;

public interface PageableInstrument {
    int getTotalPages();
    int getPageNumber();
    long getOffset();
    int getNumberOfElements();
    int getSize();
    List<Instrument> getContent();
}
