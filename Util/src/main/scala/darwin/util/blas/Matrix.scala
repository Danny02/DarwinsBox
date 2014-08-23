package darwin.util.blas

import java.nio.{ByteOrder, ByteBuffer}

import Angles._
import shapeless._
import nat._
import ops.nat._

object Matrix {

  trait Builder[X <: Nat, Y <: Nat, +M <: Matrix[X, Y]] {
    def buildEmpty(): M

    def build(f: (Int, Int) => Float): M
  }

  object Builder extends LowMatrixBuilder

  def apply[X <: Nat, Y <: Nat] = new {
    def apply[M <: Matrix[X, Y]]()(implicit b: Builder[X, Y, M]) = b.buildEmpty
  }

  def identity[X <: Nat, Y <: Nat] = new {
    def apply[M <: Matrix[X, Y]]()(implicit b: Builder[X, Y, M]) = b.build((x, y) => if (x == y) 1f else 0f)
  }

  def from[X <: Nat, Y <: Nat] = new {
    def apply[M <: Matrix[X, Y]](f: (Int, Int) => Float)(implicit b: Builder[X, Y, M]): M = b.build(f)
  }

  type Row[N <: Nat] = Matrix[N, _1] with Vector[N]
  type Column[N <: Nat] = Matrix[_1, N] with Vector[N]

  implicit class TransformOps[X <: Nat, Y <: Nat, M <: Column[Y]](m: Matrix[X, Y])(implicit ev: LT[_3, X], ev2: LT[_2, Y],
    ix: ToInt[X], iy: ToInt[Y],
    b: Vector.Builder[Y, M]) {

    private val w = ix()
    private val h = iy()

    def translate(v: Column[X]) {
      translation = m * v
    }

    def translation = b.build(m(w - 1, _))

    def translation_=(v: Vector[Y]) {
      var r = 0
      while (r < h) {
        m(w - 1, r) = v(r)
        r += 1
      }
    }
  }

  implicit def rowDropper[X <: Nat, Y <: Nat, PY <: Nat](m: Matrix[X, Y])(implicit ev: LT[_1, Y], pred: Pred.Aux[Y, PY]) = new {
    def dropRow[M <: Matrix[X, PY]](n: Int)(implicit b: Builder[X, PY, M]) = {
      b.build((x, y) => m(x, if (y >= n) y + 1 else y))
    }
  }

  implicit def columnDropper[X <: Nat, Y <: Nat, PX <: Nat](m: Matrix[X, Y])(implicit ev: LT[_1, X], pred: Pred.Aux[X, PX]) = new {
    def dropColumn[M <: Matrix[PX, Y]](n: Int)(implicit b: Builder[PX, Y, M]) = {
      b.build((x, y) => m(if (x >= n) x + 1 else x, y))
    }
  }

  implicit def minifier[X <: Nat, Y <: Nat, PX <: Nat, PY <: Nat](m: Matrix[X, Y])(implicit ev: LT[_1, X], ev2: LT[_1, Y], predX: Pred.Aux[X, PX], predY: Pred.Aux[Y, PY]) = new {
    def minor[M <: Matrix[PX, PY]](nx: Int, ny: Int)(implicit b: Builder[PX, PY, M]) = {
      b.build {
        (x, y) =>
          m(if (x >= nx) x + 1 else x,
            if (y >= ny) y + 1 else y)
      }
    }
  }

  implicit def toFloatBuffer[N <: Nat, M <: Nat](mat: Matrix[N, M])(implicit n: ToInt[N], m: ToInt[M]) = {
    val nt = n()
    val mt = m()

    val buffer = ByteBuffer
      .allocateDirect(4 * nt * mt)
      .order(ByteOrder.nativeOrder())
      .asFloatBuffer()

    var nc, mc = 0
    while (nc < nt) {
      while (mc < mt) {
        buffer.put(mat(nc, mc))
        mc += 1
      }
      nc += 1
    }
  }

  //  implicit class VectorOps[N <: Nat](v: Vector[N]) {
  //    def asColumn[M <: Column[N]](implicit b: Builder[_1, N, M]) = v match {
  //      case cv : M => cv
  //      case _ => b.build((x, y) => v(y))
  //    }
  //
  //    def asRow[M <: Row[N]](implicit b: Builder[N, _1, M]) = v match {
  //      case rv: M => rv
  //      case _ => b.build((x, y) => v(x))
  //    }
  //  }

  private def iniRot[X <: Nat, Y <: Nat](m: Matrix[X, Y], r: Radian, a: Int, b: Int) = {
    val sin = r.sinF
    val cos = r.cosF

    m(a, a) = cos
    m(b, b) = cos
    m(a, b) = -sin
    m(b, a) = sin
    m
  }

  def rotX[X <: Nat, M <: Matrix[X, X]](a: Radian)(implicit b: Builder[X, X, M], ev: LT[_1, X]) = iniRot(identity[X, X](), a, 0, 1)

  def rotY[X <: Nat, M <: Matrix[X, X]](a: Radian)(implicit b: Builder[X, X, M], ev: LT[_2, X]) = iniRot(identity[X, X](), a, 0, 1)

  def rotZ[X <: Nat, M <: Matrix[X, X]](a: Radian)(implicit b: Builder[X, X, M], ev: LT[_2, X]) = iniRot(identity[X, X](), a, 0, 1)

}

trait Matrix[X <: Nat, Y <: Nat] {

  import Matrix._

  def apply(x: Int, y: Int): Float

  def update(x: Int, y: Int, v: Float): Unit

  def update(start: Int, ar: Array[Float]): Unit

  def column[M <: Column[Y]](n: Int)(implicit b: Vector.Builder[Y, M]) = b.build(this(n, _))

  def row[M <: Row[X]](n: Int)(implicit b: Builder[X, _1, M]) = b.build((x, y) => this(x, n))

  def *[T <: Nat, M <: Matrix[T, Y], R <: Row[X], C <: Column[X]](o: Matrix[T, X])(implicit b: Builder[T, Y, M],
    b2: Builder[X, _1, R], b3: Vector.Builder[X, C]) = {
    b.build(o.column(_) dot row(_))
  }

  def transpose[M <: Matrix[Y, X]](implicit b: Builder[Y, X, M]) = b.build((x, y) => this(y, x))
}

