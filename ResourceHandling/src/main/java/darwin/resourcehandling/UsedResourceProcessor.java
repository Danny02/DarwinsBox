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

import java.util.HashSet;
import java.util.Set;

import darwin.resourcehandling.core.ResourceHandle;
import darwin.resourcehandling.handle.ClasspathFileHandler;

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
    private static final Set<String> resources = new HashSet<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(Named.class.getCanonicalName());
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
        return true;
    }

    private void appendResource(String resourcePath) {

        Messager m = processingEnv.getMessager();
        
        resources.add(resourcePath);
        m.printMessage(Kind.NOTE, resourcePath);
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
        return  e.asType().toString();
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
}
