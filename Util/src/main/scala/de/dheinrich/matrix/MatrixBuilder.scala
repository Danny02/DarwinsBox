package darwin.util.blas

import darwin.util.blas.Matrix.Builder
import shapeless._
import shapeless.nat._
import shapeless.ops.nat._

trait LowMatrixBuilder extends LowMatrixBuilder2 {

  type R3 = Vector3 with RowVector[_3]
  implicit object rowMatrix3Builder extends Builder[_3, _1, R3] {
    def build(x: Float, y: Float, z: Float) = new Vector3(x, y, z) with RowVector[_3]

    def buildEmpty() = build(0, 0, 0)

    def build(f: Int => Float): R3 = build(f(0), f(1), f(2))

    def build(f: (Int, Int) => Float) = build(i => f(i, 0))
  }

}

trait LowMatrixBuilder2 extends LowMatrixBuilder3 {
  implicit def rowMatrixBuilder[N <: Nat](implicit toInt: ToInt[N]) = new Builder[N, _1, RowVector[N]] {
    def buildEmpty() = new ArrayVector[N] with RowVector[N]

    def build(f: (Int, Int) => Float) = {
      val s = toInt()
      build(i => f(i % s, i / s))
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

trait LowMatrixBuilder3 {
  implicit def genMatrixBuilder[X <: Nat, Y <: Nat](implicit toIntX: ToInt[X], toIntY: ToInt[Y],
                                                    ev: LT[_0, X], ev2: LT[_0, Y]) = new Builder[X, Y, GenMatrix[X, Y]] {

    def buildEmpty() = new GenMatrix[X, Y]

    def build(f: (Int, Int) => Float): GenMatrix[X, Y] = {
      val m = buildEmpty()

      val w = toIntX()
      val h = toIntY()

      var x = 0
      while (x < w) {
        var y = 0
        while (y < h) {
          m(x, y) = f(x, y)
          y += 1
        }
        x += 1
      }
      m
    }

    def build(f: Int => Float): GenMatrix[X, Y] = {
      val w = toIntX()
      build((x, y) => f(w * x + y))
    }
  }
}
