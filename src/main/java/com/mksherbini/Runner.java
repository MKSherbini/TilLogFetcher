package com.mksherbini;

import com.mksherbini.util.Timer;
import com.mksherbini.model.Configuration;
import com.mksherbini.model.Environments;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

@Slf4j
public class Runner {

    private static final String XML_CONFIG_PATH = "config.xml";
    private static final String XML_VALIDATION_PATH = "config.xsd";

    public void run() {
        var timer = new Timer("Main");
        timer.mark();

        var configuration = loadConfigs();
        if (configuration == null) return;

        scrapLogs(configuration);

        timer.log();
    }

    private void scrapLogs(Configuration configuration) {
        final Scrapper scrapper = new Scrapper();

        if (configuration.getEnvironment() == Environments.ALL)
            configuration.getCorrelationIds().forEach(id ->
                    scrapper.findLogs(
                            configuration.getDate(), id)
            );
        else
            configuration.getCorrelationIds().forEach(id ->
                    scrapper.findLogs(
                            configuration.getDate(), id,
                            configuration.getEnvironment().name())
            );

        scrapper.cleanUp();
    }

    private Configuration loadConfigs() {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(XML_VALIDATION_PATH)));

            var unMarshaller = JAXBContext
                    .newInstance(Configuration.class)
                    .createUnmarshaller();
            unMarshaller.setSchema(schema);
            return (Configuration) unMarshaller
                    .unmarshal(this.getClass().getClassLoader().getResourceAsStream(XML_CONFIG_PATH));
        } catch (JAXBException | SAXException e) {
            log.error("Failed to load config.xml file, config.xml file has to exist as a resource and have valid content");
        }
        return null;
    }
}
