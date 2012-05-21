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
package darwin.resourcehandling.io;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import darwin.renderer.opengl.GLSLType;
import darwin.renderer.opengl.GlElement;
import darwin.renderer.shader.ShaderAttribute;
import darwin.renderer.shader.ShaderUniform;
import darwin.util.logging.InjectLogger;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
//TODO: Uniform/Attribut arrays parsen
public class ShaderFile implements Serializable
{

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
    public final String name, vertex, fragment, geometrie;
    public final String[] mutations;
    private List<ShaderUniform> uniforms;
    private List<ShaderAttribute> attributs;
    private List<String> sampler;

    public ShaderFile(String name, String fragment, String vertex,
            String geometrie, String... mutations)
    {
        this.name = name;
        this.vertex = vertex;
        this.fragment = fragment;
        this.geometrie = geometrie;
        this.mutations = mutations;

        uniforms = new ArrayList<>();
        attributs = new ArrayList<>();
        sampler = new ArrayList<>();

        gatherAttributs(vertex);

        gatherUniforms(vertex);
        gatherUniforms(fragment);
        gatherUniforms(geometrie);
    }

    public List<ShaderAttribute> getAttributs()
    {
        return attributs;
    }

    public List<ShaderUniform> getUniforms()
    {
        return uniforms;
    }

    public List<String> getSampler()
    {
        return sampler;
    }

    private void gatherUniforms(String source)
    {
        if (source == null) {
            return;
        }
        parseUniform(uniform.matcher(source));
        parseUniform(uniform2.matcher(source));
    }

    private void parseUniform(Matcher m)
    {
        while (m.find()) {
            List<String> n = parseNames(m.group(2));
            List<String> b = parseNames(m.group(3));

            String type = m.group(1);
            if (type.startsWith("sampler")) {
                for (String na : n) {
                    sampler.add(na);
                }
                continue;
            }

            GLSLType gtype = parseType(m.group(1));
            for (int i = 0; i < n.size(); ++i) {
                String bez = null;
                if (b.size() > i) {
                    bez = b.get(i);
                }
                GlElement ele = parseElement(gtype, bez);
                uniforms.add(new ShaderUniform(n.get(i), ele));
            }
        }
    }

    @SuppressWarnings("empty-statement")
    private void gatherAttributs(String source)
    {
        if (source == null) {
            return;
        }
        Matcher m = attribut.matcher(source);
        BitSet set = new BitSet();
        
        while (m.find()) {

            List<String> n = parseNames(m.group(2));
            List<NVP> b = parseTags(m.group(3));

            GLSLType gtype = parseType(m.group(1));
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
                attributs.add(new ShaderAttribute(n.get(i), ele, ind));
            }
        }
    }

    private List<String> parseNames(String names)
    {
        List<String> ret = new ArrayList<>(5);
        if (names != null) {
            Matcher m = elenames.matcher(names);
            while (m.find()) {
                ret.add(m.group(1));
            }
        }
        return ret;
    }

    private List<NVP> parseTags(String names)
    {
        List<NVP> ret = new ArrayList<>(5);
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

    private GlElement parseElement(GLSLType type, String name)
    {
        if (name == null || name.trim().isEmpty()) {
            name = "Element" + count++;
        }
        return new GlElement(type, name);
    }

    public static GLSLType parseType(String name)
    {
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
            default:
                //TODO gscheite fehler behandlung, eigenen exception
                throw new RuntimeException("GLSL Typ konnte nicht aufgelöst "
                        + "werden. ( Typ: " + name + " )");
        }
    }

    private final static class NVP
    {

        public final String name;
        public final Integer index;

        public NVP(String name, Integer index)
        {
            this.name = name;
            this.index = index;
        }
    }
}
