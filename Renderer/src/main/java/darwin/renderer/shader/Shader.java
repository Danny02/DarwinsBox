/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.shader;

import java.util.*;
import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import darwin.renderer.opengl.Element;
import darwin.renderer.opengl.ShaderProgramm;
import darwin.renderer.shader.uniform.MatrixSetter;
import darwin.renderer.shader.uniform.UniformSetter;
import darwin.resourcehandling.ressourcen.io.ShaderFile;
import darwin.util.math.util.GenListener;
import darwin.util.math.util.MatrixEvent;

import static darwin.renderer.GraphicContext.*;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class Shader implements GenListener<MatrixEvent>
{

    private final Map<Element, ShaderAttribute> attribute;
    private final Map<String, ShaderUniform> uniforms;
    private final Map<String, Sampler> sampler;
    private final MatrixSetter matricen;
    private ShaderProgramm programm;
    private int attrhash;
    private List<UniformSetter> usetter = new LinkedList<>();

    public Shader(ShaderFile sf)
    {
        this(sf.getAttributs(), sf.getUniforms(), sf.getSampler());
    }

    public Shader(ShaderAttribute[] attr, ShaderUniform[] uni, String[] sampler)
    {
        this(Arrays.asList(attr), Arrays.asList(uni), Arrays.asList(sampler));
    }

    public Shader(List<ShaderAttribute> attr, List<ShaderUniform> uni,
            List<String> sampler)
    {
        matricen = new MatrixSetter();
        attribute = new HashMap<>(4);
        for (ShaderAttribute sa : attr) {
            attribute.put(sa.element, sa);
        }

        uniforms = new HashMap<>(15);
        for (ShaderUniform su : uni) {
            uniforms.put(su.getName(), su);
            String b = su.getElement().bezeichnung;
            if (b != null && b.startsWith("MAT_")) {
                matricen.addUniform(su);
            }
        }

        this.sampler = new HashMap<>(4);

        for (int i = 0; i < sampler.size(); ++i) {
            this.sampler.put(sampler.get(i),
                    new Sampler(sampler.get(i), GL.GL_TEXTURE0 + i));
        }
    }

    public void ini(ShaderProgramm prog)
    {
        programm = prog;

        ini(attribute);
        ini(uniforms);
        attrhash = buildAttrHash();

        for (Sampler s : sampler.values()) {
            s.setShader(prog);
        }
    }

    @SuppressWarnings("unchecked")
    private void ini(Map<?, ? extends ShaderElement> map)
    {
        for (Iterator<?> keys = map.keySet().iterator(); keys.hasNext();) {
            ShaderElement se = map.get(keys.next());
            se.ini(programm);
            if (se.getIndex() < 0) {
                keys.remove();
            }
        }
    }

    private int buildAttrHash()
    {
        int hash = 9;
        for (ShaderAttribute sa : attribute.values()) {
            if (sa.getIndex() != -1) {
                hash = 97 * hash + sa.hashCode();
            }
        }
        return hash;
    }

    public void updateUniformData()
    {
        matricen.set();

        for (UniformSetter us : usetter) {
            us.set();
        }

        programm.use();
        GL2ES2 gl = getGL().getGL2ES2();
        for (ShaderUniform su : uniforms.values()) {
            if (su.wasChanged()) {
                gl.glUniform(su.getData());
            }
        }
    }

    public void addUSetter(UniformSetter uss)
    {
        usetter.add(uss);
    }

    public boolean isInitialized()
    {
        return programm != null;
    }

    public ShaderProgramm getProgramm()
    {
        return programm;
    }

    public ShaderUniform getUniform(String name)
    {
        ShaderUniform s = uniforms.get(name);
        return s;
    }

    public ShaderAttribute getAttribut(Element ele)
    {
        return attribute.get(ele);
    }

    public Collection<Element> getAttributElements()
    {
        return attribute.keySet();
    }

    public Sampler getSampler(String name)
    {
        return sampler.get(name);
    }

    public int getAttributsHash()
    {
        return attrhash;
    }

    @Override
    public void changeOccured(MatrixEvent t)
    {
        matricen.changeOccured(t);
    }
}
