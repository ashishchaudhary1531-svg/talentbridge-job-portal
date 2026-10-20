package com.talentbridge.model;

/** Label, count, and normalized width for a small dashboard bar chart. */
public final class BarMetric {
    private final String label;
    private final int value;
    private final int percent;

    public BarMetric(String label, int value, int percent) {
        this.label = label; this.value = value; this.percent = percent;
    }
    public String getLabel() { return label; }
    public int getValue() { return value; }
    public int getPercent() { return percent; }
}
