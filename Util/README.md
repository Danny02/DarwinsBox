# package: BLAS

A vector/matrix library in Scala with typesafe arity (`darwin.util.blas`).

## Status
This lib has still an experimental status. It works greate as a proof of concept, but still lacks a lot of standard
functionality(std. matrix funcs, not sure about im/mutability, quaternions ...). 
Another point is performance, which is not as good as it could be, but I'm confident that this
framwork has a lot of optimization opertunities.
The Next step will be macro generated optimized classes for the most common arities of some sorts.

## Basic Concepts
In wanted to create a lib which is generic to the size of matrices or vertices, but still typesafe. I use the typed 
natural numbers of the `shapeless` lib. With them I can define i.e. the types `Matrix[_4, _3]` or `Vector[_3]`.

This feature has some very nice properties:
* matrix multiplications do not only check the right size, but also know the correct size of the returned matrix
(i.e. a `Matrix[_4, _2]` times `Matrix[_3, _4]` has a return type of `Matrix[_3, _2]`)
* a matrix with an arity of 1 in one dimesion can behave as a Vector and vice versa
* a single factory method can return different optimzed implementations for each arity

## Factories

### Matrix
To create a Matrix the following factory methods are provided:
* `val m = Matrix[_4, _3]()`
* `val n = Matrix.identity[_3, _3]()`
* `val o = Matrix.from[_2, _3]((x,y) => x*y)`

#### Rotations
The factories to create rotation matrices are a little special case. A rotation matrix is always uniform(i.e. 3x3) 
and the factory methods can infer the needed arity. So you can write the following:
```scala
import Matrix._
val m = identity[_3, _2] * rotZ(0.5)
val n = identity[_4, _4] * rotX(0.6)
val o: Matrix[_3, _3] = rotY(0.7) 
```

In this lib there is a little utility object called Angles which defines the Value classes Radian and Degree with
implicit conversion between them and from ordinary numbers. For example the rotation matrix factories are defined as 
follows:

```scala
def rotN(a: Radian)

//so instead of
rotX(0.3)
//one could write
import Angles._
rotX(45.degrees)
```

### Vector
There are only two simple vector factories. The first is a special case, because the arity can be ommited.
* `val v = Vector(3, 2, 1)`
* `val u = Vector.zero[_4]()`

##Macros
Macros will be used in the future for more complex optimisation for which the type-system is not enough. 
On the other hand, there are already two utility features implemented by them.

###Swizzling
Like in the shader language GLSL one can use the letters "xyzw" or "uvst" to access the four first elements of a Vector.
```scala
val v: Vector[_3] = Vector(2, 4, 6)

val y: Float = v.y // 4f
val swiz1: Vector[_2] = v.yz // Vector(4f, 6f)
val swiz2: Vector[_4] = v.zxyy // Vector(6f, 2f, 4f, 4f)

```

###Tuple
Values of type Tuple<N> can be converted implicitly to a Vector.
```scala
val v: Vector[_3] = Vector(2, 4, 6)
val y: Float = v dot (1, 1, 1) // 12f
val y: Float = (2, 2, 2) cross (1, 1, 1) // Vector(0, 0, 0)

```


