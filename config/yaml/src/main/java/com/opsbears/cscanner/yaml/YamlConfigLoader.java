package com.opsbears.cscanner.yaml;

import com.opsbears.cscanner.core.ConfigLoader;
import com.opsbears.cscanner.core.ConnectionConfiguration;
import com.opsbears.cscanner.core.RuleConfiguration;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@ParametersAreNonnullByDefault
public class YamlConfigLoader implements ConfigLoader {
    private final String filename;

    public YamlConfigLoader(String filename) {
        this.filename = filename;
    }

    private Object loadConfigurationFile(URL url) {
        StringBuilder yamlData = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {

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
            return yaml.load(yamlData.toString());
        } catch (ParserException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Map<String, Object> loadConfigurationFileMap(URL file) {
        Object data = loadConfigurationFile(file);
        if (data instanceof Map) {
            //noinspection unchecked
            return (Map<String, Object>) data;
        } else {
            throw new RuntimeException("Invalid data type in YAML file.");
        }
    }

    private Map<Object, Object> loadConnectionIncludes(URL context, Map<Object, Object> connectionsMap) {
        connectionsMap = new HashMap<>(connectionsMap);
        if (connectionsMap.containsKey("include") && connectionsMap.get("include") instanceof Collection) {
            connectionsMap.remove("include");
            for (Object include : (Collection)connectionsMap.get("include")) {
                if (!(include instanceof String)) {
                    throw new RuntimeException("Encountered " + include.getClass().getSimpleName() + " in 'include' key in connections, expected string");
                }

                try {
                    URL newUrl = new URL(context, (String) include);
                    Map<Object, Object> newYamlData = new HashMap<>(loadConfigurationFileMap(newUrl));
                    connectionsMap.putAll(loadConnectionIncludes(newUrl, newYamlData));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return connectionsMap;
    }

    @Override
    public Map<String, ConnectionConfiguration> loadConnectionConfigurations() {
        Map<String, Object> yamlData;
        URL url;
        try {
            url = new File(filename).toURL();
            yamlData = loadConfigurationFileMap(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Map<String, ConnectionConfiguration> result = new HashMap<>();
        if (yamlData.containsKey("connections")) {
            Object connections = yamlData.get("connections");
            if (!(connections instanceof Map)) {
                throw new RuntimeException("Expected Map for key 'connections', " + connections.getClass().getSimpleName() + " found instead.");
            }
            //noinspection unchecked
            Map<Object, Object> connectionsMap = (Map<Object, Object>) connections;

            //Process includes
            connectionsMap = loadConnectionIncludes(url, connectionsMap);

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
                connectionsDataMap.remove("type");
                result.put(keyName, new ConnectionConfiguration((String)type, connectionsDataMap));
            }
        }
        return result;
    }

    private List<Object> loadRuleIncludes(URL context, List<Object> rules) {
        rules = new ArrayList<>(rules);
        List<Object> rulesToRemove = new ArrayList<>();
        List<String> urlsToLoad = new ArrayList<>();
        for (Object rule : rules) {
            if (rule instanceof Map && ((Map) rule).size() == 1 && ((Map) rule).containsKey("include")) {
                Object includeTarget = ((Map) rule).get("include");
                if (!(includeTarget instanceof String)) {
                    throw new RuntimeException("Expected string for key 'include', found " + includeTarget.getClass().getSimpleName() + " instead.");
                }

                urlsToLoad.add((String) includeTarget);
                rulesToRemove.add(rule);
            }
        }
        for (Object rule : rulesToRemove) {
            rules.remove(rule);
        }
        for (String urlString : urlsToLoad) {
            try {
                URL newUrl = new URL(context, urlString);
                Object yamlData = loadConfigurationFile(newUrl);
                if (!(yamlData instanceof Collection)) {
                    throw new RuntimeException("Expected list in included file " + newUrl.toString() + " found " + yamlData.getClass().getSimpleName() + " instead.");
                }
                rules.addAll((Collection) yamlData);
                rules = loadRuleIncludes(newUrl, rules);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return rules;
    }

    @Override
    public List<RuleConfiguration> loadRuleConfigurations() {
        List<RuleConfiguration> result = new ArrayList<>();
        Map<String, Object> yamlData = null;
        URL url;
        try {
            url = new File(filename).toURL();
            yamlData = loadConfigurationFileMap(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        if (yamlData.containsKey("rules")) {
            Object rules = yamlData.get("rules");
            if (!(rules instanceof List)) {
                throw new RuntimeException("Expected List for key 'rules', " + rules.getClass().getSimpleName() + " found instead.");
            }
            //noinspection unchecked
            List<Object> ruleList = (List<Object>) rules;

            ruleList = loadRuleIncludes(url, ruleList);

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
