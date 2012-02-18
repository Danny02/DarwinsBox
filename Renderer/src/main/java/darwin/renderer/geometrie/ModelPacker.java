/*
 *
 * *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>  *   *  This program is free software: you can redistribute it and/or modify  *  it under dheinrich.own.engineails.  *   *  You should have received a copy of the GNU General Public License  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.renderer.geometrie;

import java.util.ArrayList;
import java.util.Collection;

import darwin.renderer.geometrie.packed.RenderModel;
import darwin.renderer.geometrie.unpacked.Model;
import darwin.renderer.geometrie.unpacked.ModelObjekt;
import darwin.renderer.shader.Shader;
import darwin.renderer.shader.uniform.GameMaterial;

import static darwin.resourcehandling.resmanagment.ResourcesLoader.*;


/**
 *
 ** @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ModelPacker
{
    public static RenderModel[] packModel(ModelObjekt model) {
        Model[] submodels = model.getModels();
        RenderModel[] out = new RenderModel[submodels.length];

//        MeshModifier mm = new TangendCreator();

        for (int i = 0; i < out.length; i++) {
            Model m = submodels[i];
            GameMaterial gm = m.getMat();
            Collection<String> mut = new ArrayList<>(3);

            if (gm.alphaTex != null)
                mut.add("ALPHA_TEST");
            if (gm.specularTex != null)
                mut.add("SPECULAR_MAPPING");
            if (gm.diffuseTex != null)
                mut.add("DIFFUSE_MAPPING");
            //TODO normalmapping
//            if (gm.normalTex != null){
//                mut.add("NORMAL_MAPPING");
//                Mesh me = mm.modifie(m.getMesh());
//                m = new Model(me, gm);
//            }

            String[] mutations = new String[mut.size()];
            mut.toArray(mutations);

            Shader s = RESOURCES.getShader(gm.description, mutations);

            out[i] = new RenderModel(m, s);
        }

        return out;
    }
}
