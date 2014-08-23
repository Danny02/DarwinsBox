/*
 * Copyright (C) 2012 Daniel Heinrich <dannynullzwo@gmail.com>
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
package darwin.resourcehandling;

import darwin.resourcehandling.dependencies.annotation.InjectBundle;
import darwin.resourcehandling.dependencies.annotation.InjectResource;
import darwin.resourcehandling.handle.ClasspathFileHandler;
import darwin.resourcehandling.handle.ResourceHandle;
import darwin.resourcehandling.relative.FilerFactory;
import darwin.util.misc.FinalWrapper;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Named;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class UsedResourceProcessor extends AbstractProcessor {

    private static class MultiMap<K, V> extends HashMap<K, List<V>> {
        public void putSingle(K k, V v) {
            List<V> l = get(k);
            if (l == null) {
                l = new ArrayList<>();
                put(k, l);
            }
            l.add(v);
        }
    }

    private final static Map<Class<? extends ResourceProcessor>, List<ResourceTupel>> processed = new MultiMap<>();
    private final Set<ResourceTupel> resources = new HashSet<>();
    private static FilerFactory filer;

    private static FinalWrapper<Map<String, ResourceDependecyInspector>> inspectors;


    private Map<String, ResourceDependecyInspector> getInspectors() {
        FinalWrapper<Map<String, ResourceDependecyInspector>> wrapper = inspectors;

        if (wrapper == null) {
            synchronized (this) {
                if (inspectors == null) {
                    Map<String, ResourceDependecyInspector> instant = new HashMap<>();

                    for (ResourceDependecyInspector in : createProccessingLoader(ResourceDependecyInspector.class)) {
                        String name = in.getType().getCanonicalName();
                        if (name != null) {
                            instant.put(name, in);
                        }
                    }
                    inspectors = new FinalWrapper<>(instant);
                }
                wrapper = inspectors;
            }
        }
        return wrapper.value;
    }

    private synchronized FilerFactory getFiler() {
        if (filer == null) {
            filer = new FilerFactory(processingEnv.getFiler());
        }
        return filer;
    }

    @Override
    @SuppressWarnings("nullness")
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        for (Class a : new Class[]{Named.class, InjectResource.class, InjectBundle.class}) {
            annotations.add(a.getCanonicalName());
        }
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
        findResources(env);

        Messager m = processingEnv.getMessager();
        for (ResourceTupel tupel : resources) {
            m.printMessage(Kind.NOTE, tupel.path.toString());

//            if (!Files.exists(ClasspathFileHandler.DEV_FOLDER.resolve(tupel.path))) {
//                m.printMessage(Kind.WARNING, "Could not find resource at path: " + tupel.path.toString());
//            }
        }

        processResources();

        return true;
    }

    private void appendResource(String resourcePath, String type) {
        Path path = Paths.get(resourcePath);
        appendResource(path, type);
    }

    private String getFileExtension(Path p) {
        String fn = p.toFile().getName();
        String[] parts = fn.split("\\.");
        if (parts.length > 1) {
            return parts[parts.length - 1];
        } else {
            return null;
        }
    }

    private void appendResource(Path path, String type) {
        ResourceTupel resourceTupel = new ResourceTupel(path, type);
        if (!resources.contains(resourceTupel)) {
            resources.add(resourceTupel);
            ResourceDependecyInspector<?> get = getInspectors().get(type);
            if (get != null) {
//                for (Path object : get.getDependencys(new ClasspathFileHandler(path))) {
//                    appendResource(object, null);
//                }
            }
        }
    }

    private String getFQN(Element e) {
        return e.asType().toString();
    }

    private <T> ServiceLoader<T> createProccessingLoader(Class<T> claz) {
        JavaFileManager filemanager;
        try {
            Field context = processingEnv.getClass().getDeclaredField("context");
            context.setAccessible(true);
            Object c = context.get(processingEnv);
            assert c != null : "@SuppressWarnings nullness";
            Method declaredMethod = c.getClass().getDeclaredMethod("get", Class.class);
            filemanager = (JavaFileManager) declaredMethod.invoke(c, JavaFileManager.class);
        } catch (Throwable ex) {
            throw new RuntimeException("could not get the JavaFileManager from the Context of the JavacProcessingEviroment");
        }

        final ClassLoader classLoader = filemanager.getClassLoader(StandardLocation.CLASS_PATH);
        try {
            Field par = ClassLoader.class.getDeclaredField("parent");
            par.setAccessible(true);
            par.set(classLoader, claz.getClassLoader());
        } catch (Throwable t) {
        }

        return ServiceLoader.load(claz, classLoader);
    }

    private void findResources(RoundEnvironment env) {
        for (Element e : env.getElementsAnnotatedWith(Named.class)) {
            if (e.getKind() == ElementKind.FIELD || e.getKind() == ElementKind.PARAMETER) {
                String typeName = getFQN(e);
                if (typeName.equals(ResourceHandle.class.getCanonicalName())
                        || typeName.equals(ClasspathFileHandler.class.getCanonicalName())) {
                    appendResource(e.getAnnotation(Named.class).value(), typeName);
                }
            }
        }

        for (Element e : env.getElementsAnnotatedWith(InjectResource.class)) {
            if (e.getKind() == ElementKind.FIELD || e.getKind() == ElementKind.PARAMETER) {
                String typeName = getFQN(e);
                InjectResource annotation = e.getAnnotation(InjectResource.class);
                appendResource(annotation.file(), typeName);
            }
        }

        for (Element e : env.getElementsAnnotatedWith(InjectBundle.class)) {
            if (e.getKind() == ElementKind.FIELD || e.getKind() == ElementKind.PARAMETER) {
                String typeName = getFQN(e);
                InjectBundle annotation = e.getAnnotation(InjectBundle.class);
                for (String path : annotation.files()) {
                    appendResource(annotation.prefix() + path, typeName);
                }
            }
        }
    }

    @SuppressWarnings("nullness")
    private void processResources() {
        Set<ResourceTupel> stay = new HashSet<>();
        Set<ResourceTupel> remove = new HashSet<>();

        Messager m = processingEnv.getMessager();
        int count = 0;
        ServiceLoader<ResourceProcessor> load = createProccessingLoader(ResourceProcessor.class);
        for (ResourceProcessor rp : load) {
            ++count;
            m.printMessage(Kind.NOTE, "proccesing: " + rp.getClass().toString());

            Collection<ResourceTupel> proc = processed.get(rp.getClass());
            Set<ResourceTupel> ts = new HashSet<>();
            for (ResourceTupel tupel : resources) {
                if (isExtensionSupported(rp, tupel.path) && isClassSupported(rp, tupel.className)) {
                    if (!proc.contains(tupel)) {
                        m.printMessage(Kind.NOTE, "\t->" + tupel.path);
                        ts.add(tupel);
                        proc.add(tupel);
                    }
                }
            }

            if (ts.size() > 0) {
                Collection<ResourceTupel> p = rp.process(ts, getFiler());
                ts.removeAll(p);
                remove.addAll(p);
                stay.addAll(ts);
            }
        }
        m.printMessage(Kind.NOTE, count + " proccesors finished!");

        remove.removeAll(stay);
        for (ResourceTupel resourceTupel : remove) {
            try {
                getFiler().delete(resourceTupel.path.toString());
            } catch (IOException ex) {
            }
            m.printMessage(Kind.NOTE, "deleted: " + resourceTupel.path.toString());
        }

    }

    private boolean isClassSupported(ResourceProcessor processor, String className) {
        String[] types = null;
        if (processor.supportedResourceTypes() != null) {
            Class[] st = processor.supportedResourceTypes();
            types = new String[st.length];
            for (int i = 0; i < st.length; i++) {
                types[i] = st[i].getCanonicalName();
            }
        }

        return types == null || (className != null && Arrays.binarySearch(types, className) >= 0);
    }

    private boolean isExtensionSupported(ResourceProcessor processor, Path path) {

        String[] ex = processor.supportedFileExtensions();
        if (ex != null) {
            String extension = getFileExtension(path);
            return extension != null && Arrays.binarySearch(ex, extension) >= 0;
        }
        return true;
    }
}
