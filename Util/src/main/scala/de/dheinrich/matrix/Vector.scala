package darwin.util.blas

import darwin.util.blas.Matrix.Column
import shapeless._
import shapeless.nat._
import scala.language.experimental.macros
import shapeless.ops.tuple.Length


object Vector {

  trait Builder[N <: Nat, M <: Column[N]] extends Matrix.Builder[_1, N, M] {
    def build(f: Int => Float): M
  }

  object Builder extends LowVectorBuilder

  def apply[M <: Column[_1]](x: Float)(implicit b: Builder[_1, M]) = b.build(_ => x)

  def apply[M <: Column[_2]](x: Float, y: Float)(implicit b: Builder[_2, M]) = b.build(Seq(x, y)(_))

  def apply[M <: Column[_3]](x: Float, y: Float, z: Float)(implicit b: Builder[_3, M]) = b.build(Seq(x, y, z)(_))

  def apply[M <: Column[_4]](x: Float, y: Float, z: Float, w: Float)(implicit b: Builder[_4, M]) = b.build(Seq(x, y, z, w)(_))

  def apply[N <: Nat] = new {
    def apply[M <: Column[N]](t: Float*)(implicit b: Builder[N, M]) = b.build(t(_))

    def zero[M <: Column[N]]()(implicit b: Builder[N, M]) = b.buildEmpty()
  }

  def builder[N <: Nat] = new {
    def apply[M <: Column[N]]()(implicit b: Builder[N, M]) = b
  }

  implicit def tuple2Vector[T <: Product, N <: Nat](tuple: T)(implicit l: Length.Aux[T, N]): Column[N] = macro VectorMacros.tupleBuilder[T]

  implicit class Tuple2Vector[N <: Nat, T <: Product](val t: T)(implicit l: Length.Aux[T, N]) {
    def asVec[M <: Column[N]](implicit b: Builder[N, M]): M = macro VectorMacros.prefixTupleBuilder[T]
  }

  /*implicit class Vec3Ops(v: Vector[_3]) {
    def cross(o: Vector[_3]) = Vector(
      v.y * o.z - v.z * o.y,
      v.z * o.x - v.x * o.z,
      v.x * o.y - v.y * o.x
    )//v.yzx * o.zxy - v.zxy * o.yzx
  }*/

}

trait Vector[N <: Nat] extends BaseVector[N] {

  import Vector._

  val size: Int

  @inline
  protected def inplace(o: Vector[N])(f: (Float, Float) => Float) {
    var i = 0
    while (i < size) {
      this(i) = f(this(i), o(i))
      i += 1
    }
  }

  @inline
  protected def inplace(o: Float)(f: (Float, Float) => Float) {
    var i = 0
    while (i < size) {
      this(i) = f(this(i), o)
      i += 1
    }
  }

  @inline
  protected def onCopy[M <: ColumnVector[N]](o: Vector[N])(f: (Float, Float) => Float)(implicit b: Builder[N, M]) = {
    b.build(x => f(this(x), o(x)))
  }

  @inline
  protected def onCopy[M <: ColumnVector[N]](o: Float)(f: (Float, Float) => Float)(implicit b: Builder[N, M]) = {
    b.build(x => f(this(x), o))
  }

  def unary_-[M <: ColumnVector[N]]()(implicit b: Builder[N, M]) = {
    b.build(x => -this(x))
  }

  def -[M <: ColumnVector[N]](o: Vector[N])(implicit b: Builder[N, M]) = onCopy(o) {
    _ - _
  }

  def -[M <: ColumnVector[N]](o: Float)(implicit b: Builder[N, M]) = onCopy(o) {
    _ - _
  }

  def subI(o: Vector[N]) = inplace(o) {
    _ - _
  }

  def subI(o: Float) = inplace(o) {
    _ - _
  }

  def +[M <: ColumnVector[N]](o: Vector[N])(implicit b: Builder[N, M]) = onCopy(o) {
    _ + _
  }

  def +[M <: ColumnVector[N]](o: Float)(implicit b: Builder[N, M]) = onCopy(o) {
    _ + _
  }

  def addI(o: Vector[N]) = inplace(o) {
    _ + _
  }

  def addI(o: Float) = inplace(o) {
    _ + _
  }

  def *[M <: ColumnVector[N]](o: Vector[N])(implicit b: Builder[N, M]) = onCopy(o) {
    _ * _
  }

  def *[M <: ColumnVector[N]](o: Float)(implicit b: Builder[N, M]) = onCopy(o) {
    _ * _
  }

  def mulI(o: Vector[N]) = inplace(o) {
    _ * _
  }

  def mulI(o: Float) = inplace(o) {
    _ * _
  }

  def /[M <: ColumnVector[N]](o: Vector[N])(implicit b: Builder[N, M]) = onCopy(o) {
    _ / _
  }

  def /[M <: ColumnVector[N]](o: Float)(implicit b: Builder[N, M]) = onCopy(o) {
    _ / _
  }

  def divI(o: Vector[N]) = inplace(o) {
    _ / _
  }

  def divI(o: Float) = inplace(o) {
    _ / _
  }

  def dot(o: Vector[N]) = {
    var d = 0f
    var i = 0
    while (i < size) {
      d += apply(i) * o(i)
      i += 1
    }

    d
  }

  def lengthSq() = this dot this

  def length() = Math.sqrt(lengthSq).toFloat

  def normalizeI() = divI(length)

  def normalized[M <: ColumnVector[N]](implicit b: Builder[N, M]) = this / length

  override def toString() = ((0 until size) map apply mkString("Vector(", ", ", ")")) + getClass().getClasses().map(_.getSimpleName()).mkString(", ")
}


