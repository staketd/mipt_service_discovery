package edu.phystech.servicemash.util;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class CombinedPropertyLoader {
    private static CombinedPropertyLoader instance;
    private CombinedConfiguration configuration;

    private CombinedPropertyLoader() {
        Parameters params = new Parameters();
        CombinedConfigurationBuilder builder = new CombinedConfigurationBuilder()
                .configure(params.fileBased().setFileName("configuration.xml"));
        try {
            configuration = builder.getConfiguration();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized CombinedPropertyLoader getInstance() {
        if (instance == null) {
            instance = new CombinedPropertyLoader();
        }
        return instance;
    }

    public String getProperty(String key) {
        return (String) configuration.getProperty(key);
    }
}
