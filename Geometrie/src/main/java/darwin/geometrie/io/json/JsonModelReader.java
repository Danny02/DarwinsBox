/*
 * Copyright (C) 2013 Daniel Heinrich <dannynullzwo@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * (version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301  USA.
 */
package darwin.geometrie.io.json;

import java.io.*;
import java.lang.reflect.Field;

import darwin.annotations.ServiceProvider;
import darwin.geometrie.data.*;
import darwin.geometrie.io.*;
import darwin.geometrie.io.json.ModelBean.DataBlock;
import darwin.geometrie.io.json.ModelBean.DataElement;
import darwin.geometrie.unpacked.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.media.opengl.GL2;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@ServiceProvider(ModelReader.class)
public class JsonModelReader implements ModelReader {

    @Override
    public Model[] readModel(InputStream source) throws IOException, WrongFileTypeException {
        ObjectMapper mapper = new ObjectMapper();
        ModelBean model;
        try {
            model = mapper.readValue(source, ModelBean.class);
        } catch (Throwable t) {
            throw new IOException("JSON parse exception: \n" + t.getMessage(), t);
        }
        if (!model.isValid()) {
            throw new IOException("no valid model");
        }
        return new Model[]{convert(model)};
    }

    private Model convert(ModelBean bean) throws IOException {
        int primitiv_typ;
        try {
            Field field = GL2.class.getField(bean.type);
            primitiv_typ = field.getInt(null);
        } catch (Throwable ex) {
            throw new IOException("No known geometry type: " + bean.type);
        }
        Element[] elements = new Element[bean.data.length];
        for (int i = 0; i < elements.length; i++) {
            try {
                elements[i] = convertElement(bean.data[i]);
            } catch (IllegalArgumentException ex) {
                throw new IOException("One data element of the model could not get converted!" + bean.data[i].element.tag);
            }
        }
        DataLayout layout = new DataLayout(elements);
        VertexBuffer vb = new VertexBuffer(layout, bean.getVertexCount());
        vb.fullyInitialize();

        for (int i = 0; i < elements.length; i++) {
            vb.copyInto(0, new VertexBuffer(elements[i], bean.data[i].values));
        }

        Mesh m = new Mesh(bean.indices, vb, primitiv_typ);
        return new Model(m, null);
    }

    @Override
    public boolean isSupported(String fileExtension) {
        return fileExtension.toLowerCase().equals("json");
    }

    private Element convertElement(DataBlock dataBlock) {
        DataElement el = dataBlock.element;
        DataType dt = DataType.valueOf(el.type.toUpperCase());
        return new Element(new GenericVector(dt, el.count), el.tag);
    }
}
