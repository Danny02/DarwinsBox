package darwin.resourcehandling

import darwin.resourcehandling.cache.{MapResourceCache, ResourceCache}
import darwin.resourcehandling.handle.{ResourceBundle, ResourceHandle, FileHandleCache}
import darwin.resourcehandling.factory.ResourceFrom

/**
 * Created with IntelliJ IDEA.
 * User: daniel
 * Date: 08.11.13
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */

trait ResourceCacheComponent {
  val resourceCache: ResourceCache
}

trait ResourceComponentConfig {
  val changeNotification: Boolean
  val devFolder: Boolean
}

trait ResourceComponent {
  this: ResourceCacheComponent with ResourceComponentConfig =>

  val fileHandleCache = FileHandleCache.build().
    withChangeNotification(changeNotification).
    withDevFolder(devFolder).
    create()

  def handle(file: String) = fileHandleCache.get(file)

  def resource[T](file: String)(implicit fac: ResourceFrom[ResourceHandle, T]) = {
    resourceCache.get(fac, handle(file), false)
  }

  def unique[T](file: String)(implicit fac: ResourceFrom[ResourceHandle, T]) = {
    resourceCache.get(fac, handle(file), true)
  }

  def bundle(files: String*)(prefix: String = "", options: Seq[String] = Seq()) = {
    val handles = files map (p => fileHandleCache get (p + prefix))
    new ResourceBundle(handles.toArray, options: _*)
  }

  def resource[T](files: String*)(prefix: String = "", options: Seq[String] = Seq())(implicit fac: ResourceFrom[ResourceBundle, T]) = {
    resourceCache.get(fac, bundle(files:_*)(prefix, options), false)
  }

  def unique[T](files: String*)(prefix: String = "", options: Seq[String] = Seq())(implicit fac: ResourceFrom[ResourceBundle, T]) = {
    resourceCache.get(fac, bundle(files:_*)(prefix, options), true)
  }
}

object ResourceComponent {

  trait MappedCacheComponent extends ResourceCacheComponent {
    val resourceCache = new MapResourceCache
  }

  trait DebugConfig extends ResourceComponentConfig {
    val changeNotification = true
    val devFolder = false
  }

  type Default = ResourceComponent with MappedCacheComponent with DebugConfig
}