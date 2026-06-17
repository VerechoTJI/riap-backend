package com.riap.pbi.dbms.domain;

public enum PropertyType {
    STUDIO("套房"),
    BEDROOM("雅房"),
    APARTMENT("公寓"),
    VILLA("別墅"),
    HOUSE("整層");

    private final String displayName;

    PropertyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
