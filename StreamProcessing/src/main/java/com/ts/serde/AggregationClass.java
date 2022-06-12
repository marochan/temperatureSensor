package com.ts.serde;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.swing.text.NumberFormatter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class AggregationClass implements Serializable {

    public String timeStamp;

    public double count;

    public double sum;

    public double avg;

    public List<Double> samples;

    @JsonCreator
    public AggregationClass(@JsonProperty("timeStamp")String timeStamp,
                            @JsonProperty("count") double count,
                            @JsonProperty("sum") double sum,
                            @JsonProperty("average") double avg,
                            @JsonProperty("samples") List<Double> samples) {
        this.timeStamp = timeStamp;
        this.count = count;
        this.sum = sum;
        this.avg=avg;
        this.samples = samples;
    }

    public List<Double> getSamples() {
        return samples;
    }

    public void setSamples(List<Double> samples) {
        this.samples = samples;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }
}
