package com.riap.pbi.dbms.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PropertyTypeTest {

    @Test
    void hasAllFivePropertyTypes() {
        assertNotNull(PropertyType.STUDIO);
        assertNotNull(PropertyType.BEDROOM);
        assertNotNull(PropertyType.APARTMENT);
        assertNotNull(PropertyType.VILLA);
        assertNotNull(PropertyType.HOUSE);
    }

    @Test
    void eachTypeHasChineseDisplayName() {
        assertEquals("套房", PropertyType.STUDIO.getDisplayName());
        assertEquals("雅房", PropertyType.BEDROOM.getDisplayName());
        assertEquals("公寓", PropertyType.APARTMENT.getDisplayName());
        assertEquals("別墅", PropertyType.VILLA.getDisplayName());
        assertEquals("整層", PropertyType.HOUSE.getDisplayName());
    }
}
