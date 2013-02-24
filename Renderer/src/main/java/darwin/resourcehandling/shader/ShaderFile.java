/*
 * Copyright (C) 2012 daniel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.shader;

import java.io.Serializable;
import java.util.*;
import java.util.regex.*;

import darwin.geometrie.data.DataType;
import darwin.renderer.opengl.*;
import darwin.renderer.shader.*;
import darwin.util.logging.InjectLogger;

import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO: Uniform/Attribut arrays parsen
public class ShaderFile{

    private static final Pattern attribut = Pattern.compile(
            "\\n[^(?://)\\n]*in\\s+(\\w+)\\s+(\\w.*;)(?:.*//<(.+>))?");
    private static final Pattern uniform = Pattern.compile(
            "\\n[^(?://)\\n]*uniform\\s+(\\w+)\\s+(\\w.*;)(?:.*//<(.+>))?");
    //TODO uconst mit ifdefs im src automatisch initialisieren und zu define flags hinzufügen
    private static final Pattern uniform2 = Pattern.compile(
            "\\n[^(?://)\\n]*uconst\\s+(\\w+)\\s+(\\w.*;)(?:.*//<(.+>))?");
    private static final Pattern elenames =
                                 Pattern.compile(
            "([a-zA-Z]\\w*)(?:\\[.*\\])?(?=\\s*=|,|;|>)");
    private static final Pattern tagnames =
                                 Pattern.compile(
            "([a-zA-Z]\\w*)(?::(\\d+))?(?=\\s*=|,|>)");
    private static int count = 0;
    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    private static final long serialVersionUID = -5822433543998474334L;
    //
    public final String name;
    public final String vertex, fragment, geometrie;
    public final String[] mutations;
    private List<ShaderUniform> uniforms = new ArrayList<>();
    private List<ShaderAttribute> attributs = new ArrayList<>();
    private List<String> sampler = new ArrayList<>();

    public static class Builder {

        private String name;
        private String vertex, fragment, geometrie;
        private String[] mutations;

        public static Builder create(String name) {
            return new Builder().withName(name);
        }

        public Builder withName(String n) {
            name = n;
            return this;
        }

        public Builder withFragment(String frag) {
            fragment = frag;
            return this;
        }

        public Builder withVertex(String vert) {
            vertex = vert;
            return this;
        }

        public Builder withGeometrie(String geo) {
            geometrie = geo;
            return this;
        }

        public Builder withMutations(String... mut) {
            mutations = mut;
            return this;
        }

        public ShaderFile create() {
            return new ShaderFile(name, fragment, vertex, geometrie, mutations);
        }
    }

    public ShaderFile(String name, String fragment, String vertex,
                      String geometrie, String... mutations) {
        this.name = name;
        this.vertex = vertex;
        this.fragment = fragment;
        this.geometrie = geometrie;
        this.mutations = mutations == null ? new String[0] : mutations;

        gatherAttributs(vertex);

        gatherUniforms(vertex);
        gatherUniforms(fragment);
        gatherUniforms(geometrie);
    }

    public List<ShaderAttribute> getAttributs() {
        return attributs;
    }

    public List<ShaderUniform> getUniforms() {
        return uniforms;
    }

    public List<String> getSampler() {
        return sampler;
    }

    private void gatherUniforms(String source) {
        if (source == null) {
            return;
        }
        parseUniform(uniform.matcher(source));
        parseUniform(uniform2.matcher(source));
    }

    private void parseUniform(Matcher m) {
        while (m.find()) {
            String type = m.group(1);
            String names = m.group(2);
            String bezeichnungen = m.group(3);
            if (type != null && names != null) {
                List<String> n = parseNames(names);
                if (type.startsWith("sampler")) {
                    for (String na : n) {
                        sampler.add(na);
                    }
                } else {
                    List<String> b = parseNames(bezeichnungen);
                    GLSLType gtype = parseType(type);
                    for (int i = 0; i < n.size(); ++i) {
                        String bez = null;
                        if (b.size() > i) {
                            bez = b.get(i);
                        }
                        GlElement ele = parseElement(gtype, bez);
                        uniforms.add(new ShaderUniform(n.get(i), ele));
                    }
                }
            } else {
                //TODO possible warn that it could not parse
            }
        }
    }

    private void gatherAttributs(String source) {
        if (source == null) {
            return;
        }
        Matcher m = attribut.matcher(source);
        BitSet set = new BitSet();

        while (m.find()) {
            String type = m.group(1);
            String names = m.group(2);
            String bezeichnungen = m.group(3);
            if (names != null) {
                List<String> n = parseNames(names);
                List<NVP> b = parseTags(bezeichnungen);

                GLSLType gtype = parseType(type);
                for (int i = 0; i < n.size(); ++i) {
                    String bez = null;
                    Integer ind = null;
                    if (b.size() > i) {
                        NVP nvp = b.get(i);
                        bez = nvp.name;
                        ind = nvp.index;
                    }

                    GlElement ele = parseElement(gtype, bez);
                    if (ind != null) {
                        if (set.get(ind)) {
                            logger.warn("Eine Attribut(" + n.get(i)
                                        + ") Location(" + ind
                                        + ") wurde mehr als einmal zugewiesen."
                                        + " Dies kann zu Link Fehler führen.");
                        }
                        set.set(set.nextClearBit(ind));
                    }
                    String wtf = n.get(i);
                    attributs.add(new ShaderAttribute(wtf, ele, ind));
                }
            }
        }
    }

    private List<String> parseNames(String names) {
        List<String> ret = new ArrayList<>();
        if (names != null) {
            Matcher m = elenames.matcher(names);
            while (m.find()) {
                ret.add(m.group(1));
            }
        }
        return ret;
    }

    private List<NVP> parseTags(String names) {
        List<NVP> ret = new ArrayList<>();
        if (names != null) {
            Matcher m = tagnames.matcher(names);
            while (m.find()) {
                String index = m.group(2);
                if (index != null) {
                    ret.add(new NVP(m.group(1), Integer.parseInt(index)));
                } else {
                    ret.add(new NVP(m.group(1), null));
                }
            }
        }
        return ret;
    }

    private GlElement parseElement(GLSLType type, String name) {
        if (name == null || name.trim().isEmpty()) {
            name = "Element" + count++;
        }
        return new GlElement(type, name);
    }

    public static GLSLType parseType(String name) {
        switch (name.toLowerCase()) {
            case "float":
                return GLSLType.FLOAT;
            case "vec2":
                return GLSLType.VEC2;
            case "vec3":
                return GLSLType.VEC3;
            case "vec4":
                return GLSLType.VEC4;
            case "mat3":
                return GLSLType.MAT3;
            case "mat4":
                return GLSLType.MAT4;
            case "int":
                return GLSLType.INT;
            case "bool":
                return GLSLType.BOOL;
            default:
                if (name.startsWith("mat") && name.length() == 6) {
                    try {
                        int c = Integer.parseInt(name.substring(3, 4));
                        int r = Integer.parseInt(name.substring(5, 6));
                        return new GLSLType(DataType.FLOAT, c, r);
                    } catch (NumberFormatException ex) {
                    }
                }
                throw new RuntimeException("GLSL Typ konnte nicht aufgelöst "
                                           + "werden. ( Typ: " + name + " )");
        }
    }

    private final static class NVP {

        public final String name;
        public final Integer index;

        public NVP(String name, Integer index) {
            this.name = name;
            this.index = index;
        }
    }
}
