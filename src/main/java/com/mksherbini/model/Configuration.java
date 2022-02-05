package com.mksherbini.model;

import com.mksherbini.adapter.LocalDateAdapter;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@XmlRootElement(namespace = "MKSherbini")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Configuration {
    @XmlElement(namespace = "MKSherbini")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate date;

    @XmlElementWrapper(name = "correlationIds", namespace = "MKSherbini")
    @XmlElement(name = "id", namespace = "MKSherbini")
    private List<String> correlationIds;

    @XmlElement(namespace = "MKSherbini")
    private Environments environment;
}
