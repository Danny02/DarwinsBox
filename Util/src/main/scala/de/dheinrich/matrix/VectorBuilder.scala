package darwin.util.blas

import Vector.Builder
import shapeless._
import shapeless.nat._
import shapeless.ops.nat._

trait LowVectorBuilder extends LowVectorBuilder2 {

  implicit object matrix1Builder extends Builder[_1, Vector1] {
    def buildEmpty() = Vector1(0)

    def build(f: (Int, Int) => Float) = Vector1(f(0, 0))

    def build(f: Int => Float) = Vector1(f(0))
  }

  implicit object columnMatrix3Builder extends Builder[_3, Vector3 with ColumnVector[_3]] {
    def build(x: Float, y: Float, z: Float) = new Vector3(x, y, z) with ColumnVector[_3]

    def buildEmpty() = build(0, 0, 0)

    def build(f: Int => Float) = build(f(0), f(1), f(2))

    def build(f: (Int, Int) => Float) = build(i => f(0, i))
  }

}

trait LowVectorBuilder2 {

  implicit def columnMatrixBuilder[N <: Nat](implicit toInt: ToInt[N]) = new Builder[N, ColumnVector[N]] {
    def buildEmpty() = new ArrayVector[N] with ColumnVector[N]

    def build(f: (Int, Int) => Float) = {
      val s = toInt()
      build(i => f(i / s, i % s))
    }

    def build(f: Int => Float) = {
      val m = buildEmpty()

      var i = 0
      while (i < m.size) {
        m(i) = f(i)
        i += 1
      }

      m
    }
  }
}

