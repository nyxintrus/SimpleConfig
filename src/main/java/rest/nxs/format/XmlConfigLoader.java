package rest.nxs.format;

import org.w3c.dom.*;
import rest.nxs.config.ConfigLoader;
import rest.nxs.internal.MemoryConfig;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class XmlConfigLoader implements ConfigLoader {

    @Override
    public MemoryConfig load(File file) {
        MemoryConfig config = new MemoryConfig();

        try {
            if (!file.exists() || file.length() == 0) {
                return config;
            }

            var builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            var doc = builder.parse(file);

            doc.getDocumentElement().normalize();

            parseElement(doc.getDocumentElement(), "", config);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }

    private void parseElement(Element element, String path, MemoryConfig config) {

        NodeList children = element.getChildNodes();
        Map<String, List<Element>> grouped = new HashMap<>();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);

            if (node instanceof Element) {
                Element child = (Element) node;

                grouped.computeIfAbsent(child.getTagName(), k -> new ArrayList<>())
                        .add(child);
            }
        }

        if (grouped.isEmpty()) {
            config.set(path, element.getTextContent().trim());
            return;
        }

        for (Map.Entry<String, List<Element>> entry : grouped.entrySet()) {

            String name = entry.getKey();
            List<Element> elements = entry.getValue();

            String newPath = path.isEmpty() ? name : path + "." + name;

            if (elements.size() == 1) {
                parseElement(elements.get(0), newPath, config);
            } else {

                List<Object> list = new ArrayList<>();

                for (Element el : elements) {
                    MemoryConfig sub = new MemoryConfig();
                    parseElement(el, "", sub);

                    Object raw = sub.getRaw();

                    if (raw instanceof Map) {
                        Map map = (Map) raw;
                        if (map.size() == 1) {
                            list.add(map.values().iterator().next());
                        } else {
                            list.add(raw);
                        }
                    } else {
                        list.add(raw);
                    }
                }

                config.set(newPath, list);
            }
        }
    }

    @Override
    public void save(File file, MemoryConfig config) {
        throw new UnsupportedOperationException("XML save not implemented");
    }
}