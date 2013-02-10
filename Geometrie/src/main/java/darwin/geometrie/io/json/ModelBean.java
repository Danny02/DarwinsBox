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

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class ModelBean {

    public static class DataElement {

        public String tag, type;
        public int count;
    }

    public static class DataBlock {

        public DataElement element;
        public float[] values;
    }
    public String name, type;
    public DataBlock[] data;
    public int[] indices;

    public int getVertexCount() {
        return data[0].values.length / data[0].element.count;
    }

    public boolean isValid() {
        if (name == null || type == null) {
            return false;
        }

        if (data == null || data.length == 0) {
            return false;
        }
        int vertexCount = -1;
        for (DataBlock d : data) {
            if (d == null || d.values == null || d.element == null) {
                return false;
            }
            if (d.element.type == null || d.element.count <= 0 || d.element.count > 4) {
                return false;
            }

            int l = d.values.length;
            int c = d.element.count;
            if (l % c != 0) {
                return false;
            }
            if (vertexCount == -1) {
                vertexCount = l / c;
            } else if (vertexCount != l / c) {
                return false;
            }
            switch (d.element.type) {
                case "double":
                case "float":
                case "long":
                case "int":
                case "short":
                case "byte":
                    break;
                default:
                    return false;
            }
        }

        if (indices != null) {
            for (int i : indices) {
                if (i < 0 || i >= vertexCount) {
                    return false;
                }
            }
        }

        return true;
    }
}
