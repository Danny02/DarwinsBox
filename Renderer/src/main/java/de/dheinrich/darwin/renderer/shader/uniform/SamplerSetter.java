/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.dheinrich.darwin.renderer.shader.uniform;

import com.jogamp.opengl.util.texture.Texture;
import de.dheinrich.darwin.renderer.shader.Sampler;
import de.dheinrich.darwin.ressourcen.wrapper.TextureContainer;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class SamplerSetter implements UniformSetter {

    private final Sampler sampler;
    private final TextureContainer tex;

    public SamplerSetter(Sampler sampler, TextureContainer tex) {
        assert (sampler != null);
        assert (tex != null);
        this.sampler = sampler;
        this.tex = tex;
    }

    public SamplerSetter(Sampler sampler, Texture tex) {
        this(sampler, new TextureContainer(tex));
    }

    @Override
    public void set() {
        sampler.bindTexture(tex.getTexture());
    }
}
