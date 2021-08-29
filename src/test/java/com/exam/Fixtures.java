package com.exam;

import com.exam.common.dto.Address;
import com.exam.container.models.Container;
import com.exam.instrument.constants.InstrumentPropertyKey;
import com.exam.instrument.constants.InstrumentType;
import com.exam.instrument.models.Instrument;
import com.exam.site.models.Site;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Fixtures {
    public static Container container(String description, Long instrument) {
        return new Container(null, UUID.randomUUID(), description, instrument, null, null);
    }
    public static Container container(String description) {
        return container(description, null);
    }

    public static Instrument instrument(String name,
                                        InstrumentType instrumentType,
                                        Long site,
                                        Map<String, Object> properties,
                                        List<Container> containers) {
        return new Instrument(null, name, instrumentType, properties, site, containers, null, null);
    }

    public static Instrument computer(String name, String macAddress, Long site){
        return instrument(name, InstrumentType.Computer, site, Map.of(InstrumentPropertyKey.MacAddress.name(), macAddress), null);
    }

    public static Instrument computer(String name, String macAddress){
        return computer(name, macAddress, null);
    }

    public static Instrument freezer(String name, Long site, List<Container> containers){
        return instrument(name, InstrumentType.Freezer, site, null, containers);
    }

    public static Instrument freezer(String name, Long site){
        return freezer(name, site, null);
    }

    public static Instrument freezer(String name){
        return freezer(name, null, null);
    }

    public static Site site(String name, Address shippingAddress) {
        return new Site(null, name, shippingAddress, null, null, null);
    }

    public static Site site(String name) {
        return site(name, address());
    }

    public static Address address(){
        return new Address("123 street number", null, "city-1", "state-1", "zip-1", "country-1");
    }
}

