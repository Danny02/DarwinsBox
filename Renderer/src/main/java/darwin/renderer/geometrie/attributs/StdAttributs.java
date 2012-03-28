/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.geometrie.attributs;

import darwin.geometrie.data.Element;
import java.util.LinkedList;
import java.util.List;

import darwin.geometrie.data.DataLayout;
import darwin.renderer.opengl.*;
import darwin.renderer.shader.Shader;
import darwin.renderer.shader.ShaderAttribute;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
class StdAttributs implements AttributsConfigurator {

    private final BufferConfigs[] configs;
    private final BufferObject indice;

    public StdAttributs(Shader shader, VertexBO[] vbuffers, BufferObject indice) {
        assert shader.isInitialized() : "Shader is not initialized!";

        this.indice = indice;

        configs = new BufferConfigs[vbuffers.length];

        for (int i = 0; i < vbuffers.length; ++i) {
            DataLayout dl = vbuffers[i].layout;
            List<AttributConfig> cs = new LinkedList<>();
            for (Element ele : dl.getElements()) {
                ShaderAttribute sa = shader.getAttribut(ele);
                if (sa != null) {
                    cs.add(new AttributConfig(sa, dl.getAttribut(ele)));
                }
            }
            AttributConfig[] c = new AttributConfig[cs.size()];
            cs.toArray(c);
            configs[i] = new BufferConfigs(vbuffers[i].buffer, c);
        }
    }

    @Override
    public void prepare() {
        for (BufferConfigs bc : configs) {
            bc.prepare();
        }
        if (indice != null) {
            indice.bind();
        }
    }

    @Override
    public void disable() {
        for (BufferConfigs bc : configs) {
            bc.disable();
        }
        if (indice != null) {
            indice.disable();
        }
    }
}
