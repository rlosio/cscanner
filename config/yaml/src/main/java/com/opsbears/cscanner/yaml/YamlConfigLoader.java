package com.opsbears.cscanner.yaml;

import com.opsbears.cscanner.core.ConfigLoader;
import com.opsbears.cscanner.core.ConnectionConfiguration;
import com.opsbears.cscanner.core.RuleConfiguration;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class YamlConfigLoader implements ConfigLoader {
    private final String filename;

    public YamlConfigLoader(String filename) {
        this.filename = filename;
    }

    private Map<String, Object> loadConfigurationFile(String file) {
        StringBuilder yamlData = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                yamlData
                    .append(sCurrentLine)
                    .append("\n");
            }

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        Yaml yaml = new Yaml();
        try {
            Object data = yaml.load(yamlData.toString());
            if (data instanceof Map) {
                //noinspection unchecked
                return (Map<String, Object>) data;
            } else {
                throw new RuntimeException("Invalid data type in YAML file.");
            }
        } catch (ParserException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Map<String, ConnectionConfiguration> loadConnectionConfigurations() {
        Map<String, Object> yamlData = loadConfigurationFile(filename);
        Map<String, ConnectionConfiguration> result = new HashMap<>();
        if (yamlData.containsKey("connections")) {
            Object connections = yamlData.get("connections");
            if (!(connections instanceof Map)) {
                throw new RuntimeException("Expected Map for key 'connections', " + connections.getClass().getSimpleName() + " found instead.");
            }
            //noinspection unchecked
            Map<Object, Object> connectionsMap = (Map<Object, Object>) connections;
            for (Object key : connectionsMap.keySet()) {
                String keyName = key.toString();
                Object connectionsData = connectionsMap.get(key);
                if (!(connectionsData instanceof Map)) {
                    throw new RuntimeException("Expected Map for connection " + keyName + ", " + connectionsData.getClass().getSimpleName() + " found instead.");
                }
                //noinspection unchecked
                Map<String, Object> connectionsDataMap = (Map<String, Object>) connectionsData;

                if (!connectionsDataMap.containsKey("type")) {
                    throw new RuntimeException("Expected key 'type' on connection " + keyName);
                }
                Object type = connectionsDataMap.get("type");
                if (!(type instanceof String)) {
                    throw new RuntimeException("Expected to find a string for the key 'type' on connection " + keyName + ", " + type.getClass().getSimpleName() + " found instead");
                }
                connectionsDataMap.remove(type);
                result.put(keyName, new ConnectionConfiguration((String)type, connectionsDataMap));
            }
        }
        return result;
    }

    @Override
    public List<RuleConfiguration> loadRuleConfigurations() {
        List<RuleConfiguration> result = new ArrayList<>();
        Map<String, Object> yamlData = loadConfigurationFile(filename);
        if (yamlData.containsKey("rules")) {
            Object rules = yamlData.get("rules");
            if (!(rules instanceof List)) {
                throw new RuntimeException("Expected List for key 'rules', " + rules.getClass().getSimpleName() + " found instead.");
            }
            //noinspection unchecked
            List<Object> ruleList = (List<Object>) rules;

            for (int i = 0; i < ruleList.size(); i++) {
                Object rule = ruleList.get(i);
                if (!(rule instanceof Map)) {
                    throw new RuntimeException(("Expected Map for type " + Integer.toString(i) + ", " + rule.getClass().getSimpleName() + " found instead."));
                }
                //noinspection unchecked
                Map<Object, Object> ruleMap = (Map<Object, Object>) rule;
                if (!ruleMap.containsKey("type")) {
                    throw new RuntimeException("Expected to find key 'type' on type " + Integer.toString(i));
                }
                String type = ruleMap.get("type").toString();

                List<String> connections = new ArrayList<>();
                if (ruleMap.containsKey("connections")) {
                    Object connectionsList = ruleMap.get("connections");
                    if (!(connectionsList instanceof List)) {
                        throw new RuntimeException("Expected 'connections' key on rule " + i + " to be a list, " + connectionsList.getClass().getSimpleName() + " found");
                    }
                    for (Object connection : (List)connectionsList) {
                        connections.add(connection.toString());
                    }
                }

                Map<String, Object> parameters = new HashMap<>();
                for (Object ruleKey : ruleMap.keySet()) {
                    if (
                        !ruleKey.toString().equalsIgnoreCase("type") &&
                        !ruleKey.toString().equalsIgnoreCase("connections")
                    ) {
                        parameters.put(ruleKey.toString(), ruleMap.get(ruleKey));
                    }
                }
                result.add(new RuleConfiguration(type, connections, parameters));
            }
        }
        return result;
    }
}
