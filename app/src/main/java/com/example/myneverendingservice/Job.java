package com.example.myneverendingservice;

public class Job {

    private String date;

    private String host;

    private int count;

    private int packetSize;

    private int jobPeriod;

    private String jobType;

    public void setDate(String date) {
        this.date = date;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPacketSize(int packetSize) {
        this.packetSize = packetSize;
    }

    public void setJobPeriod(int jobPeriod) {
        this.jobPeriod = jobPeriod;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getDate() {
        return date;
    }

    public String getHost() {
        return host;
    }

    public int getCount() {
        return count;
    }

    public int getPacketSize() {
        return packetSize;
    }

    public int getJobPeriod() {
        return jobPeriod;
    }

    public String getJobType() {
        return jobType;
    }
}
