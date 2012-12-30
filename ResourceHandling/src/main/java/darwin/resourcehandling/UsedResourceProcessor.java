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

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;

import darwin.resourcehandling.dependencies.annotation.*;
import darwin.resourcehandling.handle.*;
import darwin.resourcehandling.relative.FilerFactory;

import javax.annotation.processing.*;
import javax.inject.Named;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;
import javax.tools.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class UsedResourceProcessor extends AbstractProcessor {

    private final static Map<Class<? extends ResourceProcessor>, Set<ResourceTupel>> processed = new HashMap<>();
    private final Set<ResourceTupel> resources = new HashSet<>();
    private static FilerFactory filer;
    private static Map<String, ResourceDependecyInspector> inspectors;

    private Map<String, ResourceDependecyInspector> getInspectors() {
        if (inspectors == null) {
            inspectors = new HashMap<>();

            for (ResourceDependecyInspector in : createProccessingLoader(ResourceDependecyInspector.class)) {
                inspectors.put(in.getType().getCanonicalName(), in);
            }
        }
        return inspectors;
    }

    private FilerFactory getFiler() {
        if (filer == null) {
            filer = new FilerFactory(processingEnv.getFiler());
        }
        return filer;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(Named.class.getCanonicalName());
        annotations.add(InjectResource.class.getCanonicalName());
        annotations.add(InjectBundle.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
        findResources(env);

        Messager m = processingEnv.getMessager();
        for (ResourceTupel tupel : resources) {
            m.printMessage(Kind.NOTE, tupel.path.toString());

            if (!Files.exists(ClasspathFileHandler.DEV_FOLDER.resolve(tupel.path))) {
                m.printMessage(Kind.WARNING, "Could not find resource at path: " + tupel.path.toString());
            }
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
        if (path != null || !resources.contains(resourceTupel)) {
            resources.add(resourceTupel);
            ResourceDependecyInspector<?> get = getInspectors().get(type);
            if (get != null) {
                for (Path object : get.getDependencys(new ClasspathFileHandler(path))) {
                    appendResource(object, null);
                }
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
                String[] pp = annotation.files();
                if (pp != null) {
                    for (String path : pp) {
                        appendResource(annotation.prefix() + path, typeName);
                    }
                }
            }
        }
    }

    private void processResources() {
        Set<ResourceTupel> stay = new HashSet<>();

        Messager m = processingEnv.getMessager();
        int count = 0;
        ServiceLoader<ResourceProcessor> load = createProccessingLoader(ResourceProcessor.class);
        for (ResourceProcessor rp : load) {
            ++count;
            m.printMessage(Kind.NOTE, "proccesing: " + rp.getClass().toString());

            Set<ResourceTupel> proc = processed.get(rp.getClass());
            if (proc == null) {
                proc = new HashSet<>();
                processed.put(rp.getClass(), proc);
            }

            String[] ex = rp.supportedFileExtensions();
            Class[] st = rp.supportedResourceTypes();
            String[] types = new String[st.length];
            for (int i = 0; i < st.length; i++) {
                types[i] = st[i].getCanonicalName();
            }

            Set<ResourceTupel> ts = new HashSet<>();
            for (ResourceTupel tupel : resources) {
                if (ex == null || Arrays.binarySearch(ex, getFileExtension(tupel.path)) >= 0) {
                    if (types == null || Arrays.binarySearch(types, tupel.className) >= 0) {
                        if (!proc.contains(tupel)) {
                            m.printMessage(Kind.NOTE, "\t->" + tupel.path);
                            ts.add(tupel);
                            proc.add(tupel);
                        }
                    }
                }
            }

            if (ts.size() > 0) {
                Collection<ResourceTupel> p = rp.process(ts, getFiler());
                ts.removeAll(p);
                stay.addAll(ts);
            }
        }
        m.printMessage(Kind.NOTE, count + " proccesors finished!");

        Set<ResourceTupel> delete = new HashSet<>(resources);
        delete.removeAll(stay);
        for (ResourceTupel resourceTupel : delete) {
            try {
                getFiler().delete(resourceTupel.path.toString());
            } catch (IOException ex) {
            }
            m.printMessage(Kind.NOTE, "deleted: " + resourceTupel.path.toString());
        }
    }
}
