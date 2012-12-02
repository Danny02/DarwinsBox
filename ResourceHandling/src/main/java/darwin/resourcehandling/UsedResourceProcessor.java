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

import java.nio.file.*;
import java.util.*;

import darwin.resourcehandling.dependencies.annotation.InjectBundle;
import darwin.resourcehandling.dependencies.annotation.InjectResource;
import darwin.resourcehandling.handle.ClasspathFileHandler;
import darwin.resourcehandling.handle.ResourceBundle;

import javax.annotation.processing.*;
import javax.inject.Named;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class UsedResourceProcessor extends AbstractProcessor {

//    private static final String servicePath = "META-INF/services/";
    private static final Set<Path> resources = new HashSet<>();
    private static final Map<String, ResourceDependecyInspector> inspectors = new HashMap<>();

    static {
        for (ResourceDependecyInspector in : ServiceLoader.load(ResourceDependecyInspector.class)) {
            for (String prefix : in.getSupportedFileTypes()) {
                inspectors.put(prefix, in);
            }
        }
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
        for (Element e : env.getElementsAnnotatedWith(Named.class)) {
            if (e.getKind() == ElementKind.FIELD || e.getKind() == ElementKind.PARAMETER) {
                String typeName = getFQN(e);
                if (typeName.equals(ResourceHandle.class.getCanonicalName())
                    || typeName.equals(ClasspathFileHandler.class.getCanonicalName())) {
                    appendResource(e.getAnnotation(Named.class).value());
                }
            }
        }

        for (Element e : env.getElementsAnnotatedWith(InjectResource.class)) {
            if (e.getKind() == ElementKind.FIELD || e.getKind() == ElementKind.PARAMETER) {
                String typeName = getFQN(e);
                if (typeName.equals(ResourceHandle.class.getCanonicalName())) {
                    InjectResource annotation = e.getAnnotation(InjectResource.class);
                    appendResource(annotation.prefix() + annotation.value());
                }
            }
        }

        for (Element e : env.getElementsAnnotatedWith(InjectBundle.class)) {
            if (e.getKind() == ElementKind.FIELD || e.getKind() == ElementKind.PARAMETER) {
                String typeName = getFQN(e);
                if (typeName.equals(ResourceBundle.class.getCanonicalName())) {
                    InjectBundle annotation = e.getAnnotation(InjectBundle.class);
                    String pp = annotation.value();
                    if (pp != null) {
                        for (String path : pp.split(",")) {
                            appendResource(annotation.prefix() + path);
                        }
                    }
                }
            }
        }
        return true;
    }

    private void appendResource(String resourcePath) {
        Path path = Paths.get(resourcePath);
        appendResource(path);
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

    private void appendResource(Path path) {
        if (path == null || resources.contains(path)) {
            return;
        }

        resources.add(path);
        Messager m = processingEnv.getMessager();
        m.printMessage(Kind.NOTE, path.toString());

        if (!Files.exists(ClasspathFileHandler.DEV_FOLDER.resolve(path))) {
            m.printMessage(Kind.WARNING, "Could not find resource at path: " + path.toString());
        }

        String extension = getFileExtension(path);
        ResourceDependecyInspector in = inspectors.get(extension);
        if (in != null) {
            for (Path p : in.getDependencys(new ClasspathFileHandler(path))) {
                appendResource(path);
            }
        }
//
//        String providerName = getFQN(provider).toString();
//
//        try {
//            PrintWriter pw = getWriter(servicename);
//            pw.println(providerName);
//            pw.flush();
//            m.printMessage(Diagnostic.Kind.NOTE, "Registered provider \""
//                                                 + providerName + "\" to service \"" + servicename + "\"");
//        } catch (IOException ex) {
//            Messager messager = processingEnv.getMessager();
//            messager.printMessage(Diagnostic.Kind.ERROR, ex.getLocalizedMessage());
//            for (StackTraceElement ste : ex.getStackTrace()) {
//                messager.printMessage(Diagnostic.Kind.ERROR, ste.toString());
//            }
//        }
    }

    private String getFQN(Element e) {
        return e.asType().toString();
    }
//    private PrintWriter getWriter(String file) throws IOException {
//        
//        PrintWriter pw = writer.get(file);
//        if (pw == null) {
//            try {
//                FileObject fo = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", name);
//                pw = new PrintWriter(fo.openWriter());
//            } catch (FilerException ex) {
//                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, ex.getLocalizedMessage());
//            }
//            writer.put(name, pw);
//        }
//        return pw;
//    }

    public static void main(String... args) {
        UsedResourceProcessor u = new UsedResourceProcessor();
        Path p = ClasspathFileHandler.DEV_FOLDER.resolve("resources/shader/sphere.frag");
        String ex = u.getFileExtension(p);
        System.out.println(ex);
        ResourceDependecyInspector s = inspectors.get(ex);
        System.out.println(s);
        for (Path a : s.getDependencys(new ClasspathFileHandler(p))) {
            System.out.println(a);
        };
    }
}
