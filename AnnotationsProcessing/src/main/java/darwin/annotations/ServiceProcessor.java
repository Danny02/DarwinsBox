/*
 * Copyright (C) 2012 daniel
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
package darwin.annotations;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.io.*;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.SimpleTypeVisitor7;
import javax.tools.*;

/**
 *
 * @author daniel
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ServiceProcessor extends AbstractProcessor {

    private static final String servicePath = "META-INF/services/";
    private static final Map<String, PrintWriter> writer = new HashMap<>();
    private static final TypeVisitor<Boolean, Void> noArgsVisitor =
                                                    new SimpleTypeVisitor7<Boolean, Void>() {
        @Override
        public Boolean visitExecutable(ExecutableType t, Void v) {
            return t.getParameterTypes().isEmpty();
        }
    };

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(ServiceProvider.class.getCanonicalName());
        annotations.add(MultiServiceProvider.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
        for (Element e : env.getElementsAnnotatedWith(ServiceProvider.class)) {
            try {
                e.getAnnotation(ServiceProvider.class).value().getCanonicalName();
            } catch (MirroredTypeException ex) {
                appendProvider(ex.getTypeMirror().toString(), e);
            }catch(Throwable t){}
        }
        for (Element e : env.getElementsAnnotatedWith(MultiServiceProvider.class)) {
            try {
                e.getAnnotation(MultiServiceProvider.class).value();
            } catch (MirroredTypesException ex) {
                for (TypeMirror tm : ex.getTypeMirrors()) {
                    appendProvider(tm.toString(), e);
                }
            }catch(Throwable t){}
        }
        return true;
    }

    private void appendProvider(String servicename, Element provider) {

        Messager m = processingEnv.getMessager();

        String providerName = getFQN(provider).toString();

        if (!hasNoArgsConstructor(provider)) {
            m.printMessage(Diagnostic.Kind.ERROR, "Following ServiceProvider "
                                                  + "couldn't be registered, because no public NoArgs"
                                                  + " constructor is supplied. " + providerName);
            return;
        }

        try {
            PrintWriter pw = getWriter(servicename);
            pw.println(providerName);
            pw.flush();
            m.printMessage(Diagnostic.Kind.NOTE, "Registered provider \""
                                                 + providerName + "\" to service \"" + servicename + "\"");
        } catch (IOException ex) {
            Messager messager = processingEnv.getMessager();
            messager.printMessage(Diagnostic.Kind.ERROR, ex.getLocalizedMessage());
            for (StackTraceElement ste : ex.getStackTrace()) {
                messager.printMessage(Diagnostic.Kind.ERROR, ste.toString());
            }
        }
    }

    private Name getFQN(Element e) {
        return ((TypeElement) e).getQualifiedName();
    }

    private PrintWriter getWriter(String serviceName) throws IOException {
        String name = servicePath + serviceName;
        PrintWriter pw = writer.get(name);
        if (pw == null) {
            try {
                FileObject fo = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", name);
                pw = new PrintWriter(fo.openWriter());
            } catch (FilerException ex) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, ex.getLocalizedMessage());
            }
            writer.put(name, pw);
        }
        return pw;
    }

    private boolean hasNoArgsConstructor(Element el) {
        for (Element subelement : el.getEnclosedElements()) {
            if (subelement.getKind() == ElementKind.CONSTRUCTOR
                && subelement.getModifiers().contains(Modifier.PUBLIC)) {
                TypeMirror mirror = subelement.asType();
                if (mirror.accept(noArgsVisitor, null)) {
                    return true;
                }
            }
        }
        return false;
    }
}
