package com.chinatsp.weaher.type;

public class MoJiWeatherType implements IWeatherType {
    private final String value;

    public MoJiWeatherType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
