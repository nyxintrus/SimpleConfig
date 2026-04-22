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
        try {
            var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            var doc = builder.newDocument();

            org.w3c.dom.Element root = doc.createElement("config");
            doc.appendChild(root);

            writeXml(doc, root, config.getRaw());

            var transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

            var source = new javax.xml.transform.dom.DOMSource(doc);
            var result = new javax.xml.transform.stream.StreamResult(file);

            transformer.transform(source, result);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save XML", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void writeXml(org.w3c.dom.Document doc, org.w3c.dom.Element parent, Object raw) {

        if (raw instanceof Map) {

            Map<String, Object> map = (Map<String, Object>) raw;

            for (Map.Entry<String, Object> entry : map.entrySet()) {

                String key = sanitize(entry.getKey());
                Object value = entry.getValue();

                if (value instanceof Map) {

                    org.w3c.dom.Element child = doc.createElement(key);
                    writeXml(doc, child, value);
                    parent.appendChild(child);

                } else if (value instanceof java.util.List) {

                    for (Object item : (java.util.List<?>) value) {
                        org.w3c.dom.Element child = doc.createElement(key);
                        writeXml(doc, child, item);
                        parent.appendChild(child);
                    }

                } else {

                    org.w3c.dom.Element child = doc.createElement(key);
                    child.setTextContent(String.valueOf(value));
                    parent.appendChild(child);
                }
            }

        } else {
            parent.setTextContent(String.valueOf(raw));
        }
    }

    private String sanitize(String key) {
        if (key == null || key.isBlank()) return "entry";
        return key.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}