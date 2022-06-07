package com.faza.example.dynamickafkaconsumer.util;

import org.springframework.kafka.support.Acknowledgment;

public class Acknowledgement implements Acknowledgment {


    @Override
    public void acknowledge() {
    }

    @Override
    public void nack(long sleep) {
        Acknowledgment.super.nack(sleep);
    }

    @Override
    public void nack(int index, long sleep) {
        Acknowledgment.super.nack(index, sleep);
    }
}
