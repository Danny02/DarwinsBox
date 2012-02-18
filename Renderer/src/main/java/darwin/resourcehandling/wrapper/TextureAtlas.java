/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.wrapper;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import javax.media.opengl.GL;
import javax.xml.parsers.*;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import darwin.resourcehandling.resmanagment.texture.TextureLoadJob;

import static darwin.resourcehandling.resmanagment.ResourcesLoader.*;

/**
 *
 * @author dheinrich
 */
public class TextureAtlas {

    private static class Log {

        private static Logger ger = Logger.getLogger(TextureAtlas.class);
    }
    private static final String path = "resources/Textures/";
    transient private HashMap<String, TextureAtlasElement> elements;
    transient private TextureContainer texture;

    public TextureAtlas(String file) {
        if (file.substring(file.lastIndexOf(".") + 1).equals("xml")) {
            parseXML(file);
        } else {
            parseTAI(file);
        }
    }

    public final void parseTAI(String file) {
        elements = new HashMap<>();
        InputStream is = getRessource(path + file);
        assert is != null : "TextureAtlass " + path + file + " wurde nicht gefunden";

        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }
                parseLine(line);
            }
        } catch (IOException ex) {
        }
    }

    private void parseLine(String line) {
        String[] eles = line.split(",");
        String[] name = eles[0].split("\\s+");
        if (texture == null) {
            texture = RESOURCES.getTexture(new TextureLoadJob(
                    name[1], GL.GL_LINEAR, GL.GL_CLAMP_TO_EDGE));
        }

        elements.put(name[0], new TextureAtlasElement(
                Float.parseFloat(eles[3].trim()),
                Float.parseFloat(eles[4].trim()),
                Float.parseFloat(eles[6].trim()),
                Float.parseFloat(eles[7].trim()),
                name[0]));

    }

    private void parseXML(String file) {
        elements = new HashMap<>();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();

            InputStream is = getRessource(path + file);
            Document doc = docBuilder.parse(is);
            try {
                is.close();
            } catch (IOException ex) {
            }

            Node main = doc.getFirstChild();
            NamedNodeMap nnm = main.getAttributes();
            String texname = main.getAttributes().getNamedItem("Name").
                    getNodeValue();
            texture = RESOURCES.getTexture(new TextureLoadJob(
                    texname, GL.GL_LINEAR, GL.GL_CLAMP_TO_EDGE));
            int width = Integer.parseInt(
                    nnm.getNamedItem("Width").getNodeValue());
            int heigth = Integer.parseInt(nnm.getNamedItem("Height").
                    getNodeValue());

            NodeList nl = main.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                NamedNodeMap nmm = node.getAttributes();
                if (nmm == null) {
                    continue;
                }
                String name = nmm.getNamedItem("Name").getNodeValue();
                int x = Integer.parseInt(nmm.getNamedItem("X").getNodeValue());
                int y = Integer.parseInt(nmm.getNamedItem("Y").getNodeValue());
                int w = Integer.parseInt(
                        nmm.getNamedItem("Width").getNodeValue());
                int h = Integer.parseInt(
                        nmm.getNamedItem("Height").getNodeValue());

                TextureAtlasElement tae = new TextureAtlasElement(
                        (float) x / width,
                        (float) y / heigth,
                        (float) w / width,
                        (float) h / heigth,
                        name);
                elements.put(name, tae);
            }
        } catch (SAXException ex) {
            Log.ger.fatal("XML parse error beim laden des Texture Atlases",
                    ex);
        } catch (IOException ex) {
            Log.ger.fatal(ex.getLocalizedMessage(), ex);

        } catch (ParserConfigurationException ex) {
            Log.ger.fatal(ex.getLocalizedMessage(), ex);
        }
    }

    public TextureAtlasElement getElement(String name) {
        TextureAtlasElement e = elements.get(name);
        if (e == null) {
            Log.ger.fatal("nicht existierendes TexAtlass Element angefordert.("
                    + name + ")");
        }
        return e;
    }

    public Collection<TextureAtlasElement> getElements() {
        return elements.values();
    }

    public TextureContainer getTexContainer() {
        return texture;
    }
}
