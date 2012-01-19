/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.geometrie.packed;

import de.dheinrich.darwin.renderer.geometrie.data.*;
import de.dheinrich.darwin.renderer.geometrie.unpacked.*;
import de.dheinrich.darwin.renderer.opengl.BufferObject.Target;
import de.dheinrich.darwin.renderer.opengl.BufferObject.Type;
import de.dheinrich.darwin.renderer.opengl.BufferObject.Usage;
import de.dheinrich.darwin.renderer.opengl.*;
import de.dheinrich.darwin.renderer.shader.*;
import de.dheinrich.darwin.renderer.shader.uniform.*;
import de.dheinrich.darwin.ressourcen.wrapper.*;
import java.nio.*;
import java.util.*;

/**
 * Haelt alle Render relevanten Attribute eines 3D Modelles.
 * Rendert ein Modell nach diesen Attributen
 * @author Daniel Heinrich
 */
public class RenderModel implements Shaded, Cloneable
{
    private GameMaterial material;
    private RenderMesh rbuffer;
    private Shader shader;
    private final Set<UniformSetter> uniforms = new HashSet<>();
    private AsyncIni initiator = null;

    public RenderModel(RenderMesh rbuffer, Shader shader,
                       GameMaterial mat) {
        this.rbuffer = rbuffer;
        material = mat;
        setShader(shader);
    }

    public RenderModel(Model model, final Shader shader) {
        material = model.getMat();

        final Mesh m = model.getMesh();
        VertexBuffer vb = m.getVertices();
        final VertexBO vbo = new VertexBO(vb);

        final BufferObject indice = new BufferObject(
                Target.ELEMENT_ARRAY);

        Buffer ind = IntBuffer.wrap(m.getIndicies());
        indice.bind();
        {
            indice.bufferData(ind, Type.STATIC, Usage.DRAW);
        }
        indice.disable();
        initiator = new AsyncIni()
        {
            @Override
            public void ini() {
                rbuffer = new RenderMesh(shader, m.getPrimitiv_typ(), indice,
                                         vbo);
            }
        };
        setShader(shader);
    }

    @Override
    public void render() {
        if (shader.isInitialized()) {
            init();
            for (UniformSetter us : uniforms)
                us.set();
            shader.updateUniformData();
            rbuffer.render();
        }
    }

    private void init() {
        if (initiator != null) {
            initiator.ini();
            initiator = null;
        }
    }

    public void setShader(Shader shader) {
        this.shader = shader;
        if (material != null) {
            ShaderMaterial smaterial = new ShaderMaterial(shader, material);
            for (UniformSetter us : smaterial.getSetter())
                uniforms.add(us);
        }
    }

    public void addSamplerSetter(String s, TextureContainer tc) {
        uniforms.add(new SamplerSetter(shader.getSampler(s), tc));
    }

    public void addUniformSetter(UniformSetter us) {
        uniforms.add(us);
    }

    @Override
    public Shader getShader() {
        return shader;
    }

    @Override
    public RenderModel clone() {
        return new RenderModel(rbuffer.clone(), shader, material);
    }
}
