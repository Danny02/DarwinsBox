/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package darwin.util.blas

import java.nio.ByteBuffer
import shapeless.nat._

trait MappedObject[T]{
  def size(): Int
  def map(b: ByteBuffer): T
}

object MappedObject{
  
  def alloc[T](n: Int)(implicit m: MappedObject[T]): IndexedSeq[T] = {
    val b = ByteBuffer.allocateDirect(n * m.size)
    new MappedBuffer[T](b)
  }
  
  implicit object MappedVec extends MappedObject[Vec]{
    def size() = 12
    def map(b: ByteBuffer) = BVec(b)
  }
}

class MappedBuffer[T](b: ByteBuffer)(implicit mapped: MappedObject[T]) extends IndexedSeq[T]
{
  def apply(i: Int) = {
    b.position(i * mapped.size)
    mapped.map(b.slice)
  }
  
  def length() = b.capacity / mapped.size
}

trait Vec extends Vector[_3]{  
  var x: Float
  var y: Float 
  var z: Float 
  
  val size = 3

  def apply(i: Int)= i match {
    case 0 => x
    case 1 => y
    case 2 => z
  }

  def update(i: Int, v: Float) = i match {
    case 0 => x = v
    case 1 => y = v
    case 2 => z = v
  }
  
  def copyInto(v: Vector[_3])
  {
    x = v(0)
    y = v(1)
    z = v(2)
  }
}

case class BVec(buffer: ByteBuffer) extends Vec{
  override def x() = buffer.getFloat(0)
  override def x_=(f: Float) = buffer.putFloat(0, f)
  
  def y() = buffer.getFloat(4)
  def y_=(f: Float) = buffer.putFloat(0, f)
  
  def z() = buffer.getFloat(8)
  def z_=(f: Float) = buffer.putFloat(0, f)
}

class BufferMapper {  
  val vecs = MappedObject.alloc[Vec](100)
}

//object BufferMapper{
//  def main(args: Array[String]){
//    bench(Bench.myPlus)
//    bench(Bench.bufPlus)
//    bench(Bench.bufPlusCopy)
//  }
//
//  def bench(be: Bench[_]) {
//
//    val count = 100
//
//    Bench.runs(10) { () =>
//      be.time(count)
//    }
//
//    timed { () =>
//      be.time(count)
//    }
//
//    def timed(f: () => Any) = {
//      val time = System.nanoTime()
//      val a = f()
//      val diff = System.nanoTime() - time
//      println(diff / 1000000f + "ms")
//    }
//
//    //Bench.test
//  }
//}
