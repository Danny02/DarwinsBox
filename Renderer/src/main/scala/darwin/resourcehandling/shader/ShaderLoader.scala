/*
 * Copyright (C) 2012 some
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
package darwin.resourcehandling.shader

import java.io._
import java.nio.file._
import darwin.renderer.opengl._
import darwin.renderer.shader._
import darwin.resourcehandling.factory.ResourceFromBundle
import darwin.resourcehandling.handle._
import darwin.resourcehandling.shader.brdf._
import com.google.common.base.Optional
import javax.media.opengl._
import darwin.resourcehandling.ResourceComponent
import darwin.renderer.{GProfile, GraphicComponent}
import darwin.util.logging.LoggingComponent

/**
 *
 * @author some
 */
object ShaderLoaderComponent {
  val INCLUDE_PREFIX = "#pragma include"
  val BRDF_PREFIX = "#pragma brdf"
  val BRDF_PATH_PREFIX = "resources/brdfs/"
  val SHADER_PATH_PREFIX = "resources/shaders/"
  val SHADER_PATH = Paths.get(SHADER_PATH_PREFIX)
}

trait ShaderLoaderComponent extends LoggingComponent{
  this: ShaderComponent with ShaderObjektComponent with ResourceComponent
    with GraphicComponent with GProfile[GL2GL3] =>

  import ShaderLoaderComponent._

  implicit val shaderLoader = new ResourceFromBundle[Shader] {

    def create(bundle: ResourceBundle): Shader = {
      val builder: ShaderFile.Builder = new ShaderFile.Builder

      val iter = Iterator.tabulate(bundle.getCount)(bundle get _)
      val facs = Iterator(builder.withFragment _, builder.withVertex _, builder.withGeometrie _)

      (iter zip facs) foreach (t => t._2(getData(t._1.getStream)))

      builder.withName(bundle.toString).withMutations(bundle.getOptions: _*)
      val file: ShaderFile = builder.create
      val shader: Shader = createShader(file)
      context.invoke(false, {
        glad =>
          try {
            val compiledShader: ShaderProgramm = compileShader(file)
            val old: ShaderProgramm = shader.getProgramm
            shader.ini(compiledShader)
            logger.info("Shader " + file.name + " was succesfully compiled!")
            if (old != null) {
              old.delete
            }
          }
          catch {
            case ex: Throwable => {
              logger.warn("" + ex.getLocalizedMessage)
            }
          }
          true
      })

      return shader
    }

    def update(bundle: ResourceBundle, changed: ResourceHandle, shader: Shader) {
      try {
        var d: String = null
        var t: ShaderType = null
        bundle.getCount match {
          case _ =>
          case 3 if bundle.get(2) eq changed =>
            d = getData(bundle.get(2).getStream)
            t = ShaderType.Geometrie
          case 2 if (bundle.get(1) eq changed) => {
            d = getData(bundle.get(1).getStream)
            t = ShaderType.Vertex
          }
          case 1 if (bundle.get(0) eq changed) => {
            d = getData(bundle.get(0).getStream)
            t = ShaderType.Fragment
          }
          case 0 =>
            return
        }
        val `type`: ShaderType = t
        val data: String = d

        context.invoke(false, {
          glad =>
            try {
              val gl: GL2GL3 = glad.getGL.getGL2GL3
              val so: ShaderObjekt = createSObject(`type`, data, bundle.getOptions: _*)
              val po: Int = shader.getProgramm.getPObject
              val ret: Array[Int] = new Array[Int](1)
              gl.glGetProgramiv(po, GL2ES2.GL_ATTACHED_SHADERS, ret, 0)
              val shaderNames: Array[Int] = new Array[Int](ret(0))
              gl.glGetAttachedShaders(po, ret(0), ret, 0, shaderNames, 0)

              for (i <- shaderNames) {
                gl.glGetShaderiv(i, GL2ES2.GL_SHADER_TYPE, ret, 0)
                if (ret(0) == so.getType.glConst) {
                  gl.glDetachShader(po, i)
                  gl.glDeleteShader(i)
                }
              }

              gl.glAttachShader(po, so.getShaderobjekt)
              gl.glLinkProgram(po)
              shader.ini(shader.getProgramm)
              val error: Optional[String] = shader.getProgramm.verify
              if (error.isPresent) {
                logger.warn(error.get)
                shader.ini(getFallBack.getProgramm)
              }
              else {
                logger.info("Shader " + bundle + " was succesfully updated!")
              }
            }
            catch {
              case ex: Throwable => {
                logger.warn("" + ex.getLocalizedMessage)
              }
            }
            true
        })
      }
      catch {
        case ex: IOException => {
          logger.warn("" + ex.getLocalizedMessage)
        }
      }
    }

    def getFallBack: Shader = {
      try {
        return empty
      }
      catch {
        case ex: IOException => {
          throw new RuntimeException("Could not load fallback shader!")
        }
      }
    }

    def compileShader(sfile: ShaderFile): ShaderProgramm = {
      val sobjects = for (st <- ShaderType.values()) yield {
        val src = st.extract(sfile)

        try {
          Right(Option(src).map(createSObject(st, _, sfile.mutations: _*)))
        }
        catch {
          case ex: BuildException => {
            Left((st, ex))
          }
        }
      }

      val errors = sobjects.flatMap(_.left.toOption)
      if(!errors.isEmpty){
        val es = errors.map(e => s"\t${e._2.getErrorType.name} error in ${e._1.name}: ${e._2.getMessage}")
        throw new RuntimeException(s"Error(s) in Shader {${sfile.name}}:\n" + es.mkString("\n"))
      }

      val sos = sobjects.flatMap(_.right.toOption).flatten
      val builder = sos.foldLeft(new ShaderProgramBuilder)((prog, obj) => prog `with` obj)

      builder.`with`(sfile.getAttributs).link(context.gl)
    }

    def createSObject(target: ShaderType, source: String, mut: String*): ShaderObjekt = {
      val sources = (glslVersion +: mut.map(m => s"#define $m\n") :+ source).toArray
      return createShaderObject(target, sources)
    }

    def getData(file: InputStream): String = {
      if (file == null) {
        return null
      }

      val lines = scala.io.Source.fromInputStream(file).getLines()
      var end: List[String] = Nil

      val prepared = (lines.filterNot(_ startsWith "#version").map {
        case l if l startsWith INCLUDE_PREFIX => {
          val path: String = l.substring(INCLUDE_PREFIX.length).trim
          getData(handle(SHADER_PATH_PREFIX + path).getStream)
        }
        case l if l startsWith BRDF_PREFIX => {
          val path: String = l.substring(BRDF_PREFIX.length).trim
          val stream: InputStream = handle(BRDF_PATH_PREFIX + path + ".brdf").getStream
          val brdf = BrdfReader.readBrdf(stream, path)
          stream.close()

          end :+= brdf.getCode

          brdf.getFunctionTemplate
        }
        case l => l
      } ++ end).mkString("\n")

      file.close()

      prepared
    }
  }

  lazy val empty: Shader = resource("Empty.frag", "Empty.vert")(SHADER_PATH_PREFIX)
}