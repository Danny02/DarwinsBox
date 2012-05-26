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
package darwin.renderer.geometrie;

import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;

import darwin.geometrie.unpacked.Material;
import darwin.geometrie.unpacked.Model;
import darwin.renderer.geometrie.packed.RenderModel;
import darwin.renderer.geometrie.packed.RenderModel.RenderModelFactory;
import darwin.renderer.shader.Shader;
import darwin.resourcehandling.resmanagment.ResourcesLoader;
import darwin.resourcehandling.resmanagment.texture.ShaderDescription;

/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
@Singleton
public class ModelPacker
{

    private final RenderModelFactory factory;
    private final ResourcesLoader loader;

    @Inject
    public ModelPacker(RenderModelFactory factory, ResourcesLoader loader)
    {
        this.factory = factory;
        this.loader = loader;
    }

    public RenderModel[] packModel(Model[] models, ShaderDescription description)
    {
        RenderModel[] out = new RenderModel[models.length];

//        MeshModifier mm = new TangendCreator();

        for (int i = 0; i < out.length; i++) {
            Model m = models[i];
            Material gm = m.getMat();
            Collection<String> mut = new ArrayList<>(3);

            if (gm.alphaTex != null) {
                mut.add("ALPHA_TEST");
            }
            if (gm.specularTex != null) {
                mut.add("SPECULAR_MAPPING");
            }
            if (gm.diffuseTex != null) {
                mut.add("DIFFUSE_MAPPING");
            }
            //TODO normalmapping
//            if (gm.normalTex != null){
//                mut.add("NORMAL_MAPPING");
//                Mesh me = mm.modifie(m.getMesh());
//                m = new Model(me, gm);
//            }

            String[] mutations = new String[mut.size()];
            mut.toArray(mutations);

            Shader s = loader.getShader(description, mutations);

            out[i] = factory.create(m, s);
        }

        return out;
    }
}
