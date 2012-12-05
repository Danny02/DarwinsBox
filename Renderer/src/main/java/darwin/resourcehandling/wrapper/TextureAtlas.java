///*
// * Copyright (C) 2012 daniel
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package darwin.resourcehandling.wrapper;
//
//import java.io.*;
//import java.util.Collection;
//import java.util.HashMap;
//
//import darwin.resourcehandling.handle.FileHandlerFactory;
//import darwin.resourcehandling.resmanagment.texture.TextureLoadJob;
//import darwin.resourcehandling.resmanagment.texture.TextureLoadJob.TextureJobFactory;
//import darwin.util.logging.InjectLogger;
//
//import com.google.inject.assistedinject.Assisted;
//import com.google.inject.assistedinject.AssistedInject;
//import javax.media.opengl.GL;
//import javax.xml.parsers.*;
//import org.slf4j.Logger;
//import org.slf4j.helpers.NOPLogger;
//import org.w3c.dom.*;
//import org.xml.sax.SAXException;
//
///**
// *
// * @author dheinrich
// */
//public class TextureAtlas {
//
//    public interface AtlasFactory {
//
//        public TextureAtlas create(String file);
//    }
//    private static final String path = "resources/Textures/";
//    @InjectLogger
//    private Logger logger = NOPLogger.NOP_LOGGER;
//    private HashMap<String, TextureAtlasElement> elements;
//    private TextureContainer texture;
//    private final TextureJobFactory factory;
//    private final FileHandlerFactory filer;
//
//    @AssistedInject
//    public TextureAtlas(FileHandlerFactory loader, TextureJobFactory factory,
//                        @Assisted String file) {
//        this.factory = factory;
//        filer = loader;
//        if (file.substring(file.lastIndexOf(".") + 1).equals("xml")) {
//            parseXML(file);
//        } else {
//            parseTAI(file);
//        }
//    }
//
//    public final void parseTAI(String file) {
//        elements = new HashMap<>();
//
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(filer.create(path + file).getStream()))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                if (line.isEmpty()) {
//                    continue;
//                }
//                if (line.charAt(0) == '#') {
//                    continue;
//                }
//                parseLine(line);
//            }
//        } catch (IOException ex) {
//        }
//    }
//
//    private void parseLine(String line) {
//        String[] eles = line.split(",");
//        String[] name = eles[0].split("\\s+");
//        if (texture == null) {
//            TextureLoadJob loadJob = factory.create(name[1], GL.GL_LINEAR, GL.GL_CLAMP_TO_EDGE);
//            texture = loader.getTexture(loadJob);
//        }
//
//        elements.put(name[0], new TextureAtlasElement(
//                Float.parseFloat(eles[3].trim()),
//                Float.parseFloat(eles[4].trim()),
//                Float.parseFloat(eles[6].trim()),
//                Float.parseFloat(eles[7].trim()),
//                name[0]));
//
//    }
//
//    private void parseXML(String file) {
//        elements = new HashMap<>();
//
//        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder docBuilder;
//        try {
//            docBuilder = docFactory.newDocumentBuilder();
//
//            InputStream is = filer.create(path + file).getStream();
//            Document doc = docBuilder.parse(is);
//            try {
//                is.close();
//            } catch (IOException ex) {
//            }
//
//            Node main = doc.getFirstChild();
//            NamedNodeMap nnm = main.getAttributes();
//            String texname = main.getAttributes().getNamedItem("Name").
//                    getNodeValue();
//            TextureLoadJob loadJob = factory.create(texname, GL.GL_LINEAR, GL.GL_CLAMP_TO_EDGE);
//            texture = loader.getTexture(loadJob);
//            int width = Integer.parseInt(
//                    nnm.getNamedItem("Width").getNodeValue());
//            int heigth = Integer.parseInt(nnm.getNamedItem("Height").
//                    getNodeValue());
//
//            NodeList nl = main.getChildNodes();
//            for (int i = 0; i < nl.getLength(); i++) {
//                Node node = nl.item(i);
//                NamedNodeMap nmm = node.getAttributes();
//                if (nmm == null) {
//                    continue;
//                }
//                String name = nmm.getNamedItem("Name").getNodeValue();
//                int x = Integer.parseInt(nmm.getNamedItem("X").getNodeValue());
//                int y = Integer.parseInt(nmm.getNamedItem("Y").getNodeValue());
//                int w = Integer.parseInt(
//                        nmm.getNamedItem("Width").getNodeValue());
//                int h = Integer.parseInt(
//                        nmm.getNamedItem("Height").getNodeValue());
//
//                TextureAtlasElement tae = new TextureAtlasElement(
//                        (float) x / width,
//                        (float) y / heigth,
//                        (float) w / width,
//                        (float) h / heigth,
//                        name);
//                elements.put(name, tae);
//            }
//        } catch (SAXException ex) {
//            logger.error("XML parse error beim laden des Texture Atlases",
//                         ex);
//        } catch (IOException | ParserConfigurationException ex) {
//            logger.error(ex.getLocalizedMessage(), ex);
//        }
//    }
//
//    public TextureAtlasElement getElement(String name) {
//        TextureAtlasElement e = elements.get(name);
//        if (e == null) {
//            logger.error("nicht existierendes TexAtlass Element angefordert.({})", name);
//        }
//        return e;
//    }
//
//    public Collection<TextureAtlasElement> getElements() {
//        return elements.values();
//    }
//
//    public TextureContainer getTexContainer() {
//        return texture;
//    }
//}
