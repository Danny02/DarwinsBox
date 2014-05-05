package darwin.util.blas

import scala.language.dynamics
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import shapeless._


trait BaseVector[N <: Nat] extends Dynamic {

  def apply(i: Int): Float

  @inline
  def selectDynamic(name: String) : Vector[_] = macro VectorMacros.tupleApply[N]

  @inline
  def x = apply(0)

  def update(i: Int, v: Float): Unit

  def updateDynamic(name: String)(value: Float) : Unit = macro VectorMacros.tupleUpdate[N]

  //  def updateDynamic[M <: Nat](name: String)(value: Vector[M]) = macro VectorMacros.tupleUpdate[N, M]

  @inline
  def x_=(v: Float) {
    update(0, v)
  }
}

object VectorMacros {

  val alias1 = Seq('x', 'y', 'z', 'w')
  val alias2 = Seq('u', 'v', 's', 't')

  object Single {
    def unapply(name: String): Option[Int] =
      if (name.length == 1)
        Some(Seq(alias1, alias2).map(_.indexOf(name.charAt(0))).max).filter(_ > 0)
      else
        None
  }

  object Multi {
    val regex = (alias1.mkString("([", "", "]+)") + '|' + alias2.mkString("([", "", "]+)")).r

    def unapply(name: String): Option[Seq[Int]] = name match {
      case regex(l, r) => Option(l) orElse Option(r) map {
        _.map {
          id => Seq(alias1, alias2).map(_.indexOf(id)).max
        }
      }
      case _ => None
    }
  }

  def tupleApply[A <: Nat : c.WeakTypeTag](c: Context)(name: c.Expr[String]): c.Tree = {
    import c.universe._

    val typeName = weakTypeTag[A].tpe.toString
    val max = typeName.substring(typeName.lastIndexOf(".") + 2).toInt

    def check[T](n: Int) = if (n >= max) c.error(name.tree.pos, s"""trying to access the vector field with index $n while there are only $max elements.""")

    val Literal(Constant(s_name: String)) = name.tree

    val vec = c.prefix

    s_name match {
      case Single(id) =>
        check(id)
        q"$vec($id)"
      case Multi(ids) =>
        ids foreach check
        val tmp = TermName(c.freshName())
        val elements = ids.map(id => q"$tmp($id)")
        q"""{val $tmp = $vec
        darwin.util.blas.Vector(..$elements)}"""
      case _ =>
        c.error(name.tree.pos, s"""must be [xyzw] or [uvst] not $s_name""")
        ???
    }
  }

  def tupleUpdate[A <: Nat : c.WeakTypeTag](c: Context)(name: c.Expr[String])(value: c.Expr[Float]): c.Tree = {
    import c.universe._

    val typeName = weakTypeTag[A].tpe.toString
    val max = typeName.substring(typeName.lastIndexOf(".") + 2).toInt

    def check[T](n: Int) = if (n >= max) c.error(name.tree.pos, s"""trying to access the vector field with index $n while there are only $max elements.""")

    val Literal(Constant(s_name: String)) = name.tree
    s_name match {
      case Single(id) =>
        check(id)
        q"${c.prefix}($id) = $value"
      case _ =>
        c.error(name.tree.pos, s"""must be [xyzw] or [uvst] not $s_name""")
        ???
    }
  }

  val tupleRegx = ".*Tuple(\\d+)".r

  def tupleBuilder[T <: Product : c.WeakTypeTag](c: Context)(tuple: c.Tree)(l: c.Tree): c.Tree = {
    import c.universe._

    val typeName = weakTypeTag[T].tpe.typeConstructor.toString // to get Tuple2 from (Float, Float)

    val max = tupleRegx.findFirstMatchIn(typeName) match {
      case Some(m) => m.group(1).toInt
      case None => c.error(tuple.pos, s"$typeName not of Type TupleX"); -1
    }

    val tmp = TermName(c.freshName())

    val values = (1 to max).map {
      i =>
        val n = TermName("_" + i)
        q"$tmp.$n"
    }
    q"""{val $tmp = $tuple
    darwin.util.blas.Vector(..$values)}"""
  }

  def prefixTupleBuilder[T <: Product : c.WeakTypeTag](c: Context)(b: c.Tree): c.Tree = {
    import c.universe._
    tupleBuilder(c)(q"${c.prefix}.t")(b)
  }
}
