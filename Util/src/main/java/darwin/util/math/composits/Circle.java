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
package darwin.util.math.composits;

import darwin.util.math.base.Line;
import darwin.util.math.base.vector.*;

import static java.lang.Math.sqrt;

/**
 *
 * @author daniel
 */
public class Circle
{
    private final ImmutableVector<Vector2> center;
    private final float radius;

    public Circle(ImmutableVector<Vector2> center, float radius)
    {
        this.center = center;
        this.radius = radius;
    }

    public Circle getScaledCircle(float scale)
    {
        return new Circle(center, radius * scale);
    }

    public Circle getExtrudedCircle(float amount)
    {
        return new Circle(center, Math.abs(radius - amount));
    }

    public float getRadius()
    {
        return radius;
    }

    public ImmutableVector<Vector2> getCenter()
    {
        return center;
    }

    /**
     * @param line < p/>
     * <p/>
     * @return <b>null</b> if the line does not intersect with the circle
     */
    public LineSegment<Vector2> getIntersection(Line<Vector2> line)
    {
        assert !contains(line.getStartingPoint());

        Vector2 toCircle = center.clone().sub(line.getStartingPoint());
        float distSquare = (float) toCircle.lengthQuad();

        if (distSquare == 0) {
            toCircle = center.clone().sub(line.getStartingPoint().clone().add(line.getDirection()));
            distSquare = (float) toCircle.lengthQuad();
        }

        float t = toCircle.dot(line.getDirection());

        float tmp = radius * radius - distSquare + t * t;
        if (tmp < 0) {
            return null;//no intersection
        }
        float f = (float) sqrt(tmp);

        return new LineSegment<>(line.getDirection().clone().mul(t - f).add(line.getStartingPoint()),
                                 line.getDirection().clone().mul(t + f).add(line.getStartingPoint()));
    }

    public boolean contains(ImmutableVector<Vector2> point)
    {
        return point.clone().sub(center).length() <= radius;
    }
}
