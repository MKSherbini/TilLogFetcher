package com.mksherbini.scrapper.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    private static final String CUSTOM_FORMAT_STRING = "yyyy-MM-dd";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CUSTOM_FORMAT_STRING);

    @Override
    public String marshal(LocalDate v) {
        return v.format(formatter);
    }

    @Override
    public LocalDate unmarshal(String v) throws ParseException {
        return LocalDate.parse(v, formatter);
    }
}
