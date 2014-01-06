package darwin.util.blas

object Angles {
  implicit class DoubleAngle(d: Double) {
    def degree() = new Degree(d)
    def radian() = new Radian(d)
  }

  implicit class Degree(val f: Double) extends AnyVal
  implicit class Radian(val f: Double) extends AnyVal

  implicit class AngleFuncs(r: Radian) {
    def sinF() = Math.sin(r).toFloat
    def cosF() = Math.cos(r).toFloat
  }

  implicit def degree2Double(d: Degree): Double = d.f
  implicit def radian2Double(r: Radian): Double = r.f

  private val toRadian = 180 / Math.PI
  private val toDegree = Math.PI / 180

  implicit def toDegree(r: Radian): Degree = r * toRadian
  implicit def toRadian(d: Degree): Radian = d * toDegree
}