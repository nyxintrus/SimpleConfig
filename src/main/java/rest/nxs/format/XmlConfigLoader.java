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

            if (node instanceof Element child) {
                grouped
                        .computeIfAbsent(child.getTagName(), k -> new ArrayList<>())
                        .add(child);
            }
        }

        // leaf node
        if (grouped.isEmpty()) {
            config.set(path, element.getTextContent().trim());
            return;
        }

        for (var entry : grouped.entrySet()) {

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

                    // FIX: unwrap broken {=value} maps
                    if (raw instanceof Map<?, ?> map && map.size() == 1) {
                        list.add(map.values().iterator().next());
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
        try {
            var factory = DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();

            var doc = builder.newDocument();

            Element root = doc.createElement("config");
            doc.appendChild(root);

            buildXml(doc, root, config.getRaw());

            var transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

            var source = new javax.xml.transform.dom.DOMSource(doc);
            var result = new javax.xml.transform.stream.StreamResult(file);

            transformer.transform(source, result);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save XML config", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void buildXml(Document doc, Element parent, Object raw) {

        if (raw instanceof Map<?, ?> map) {

            for (var entry : map.entrySet()) {

                String key = sanitizeXmlTag(String.valueOf(entry.getKey()));
                Object value = entry.getValue();

                if (value instanceof List<?> list) {

                    for (Object item : list) {
                        Element child = doc.createElement(key);
                        buildXml(doc, child, item);
                        parent.appendChild(child);
                    }

                } else {
                    Element child = doc.createElement(key);
                    buildXml(doc, child, value);
                    parent.appendChild(child);
                }
            }

        } else {
            parent.setTextContent(raw == null ? "" : String.valueOf(raw));
        }
    }

    private String sanitizeXmlTag(String key) {
        if (key == null || key.isBlank()) {
            return "entry";
        }

        return key.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}