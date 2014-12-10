/*
 * Copyright (C) 2012 Daniel Heinrich <dannynullzwo@gmail.com>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3.0 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.annotations;

import java.io.*;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.tools.*;

import static javax.tools.Diagnostic.Kind.*;
import static darwin.annotations.CheckModus.SAFE;
import static javax.lang.model.SourceVersion.RELEASE_6;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
@SupportedSourceVersion(RELEASE_6)
public class ServiceProcessor extends AbstractProcessor {

  private static final String servicePath = "META-INF/services/";
  private static final Map<String, PrintWriter> writer = new HashMap<>();
  private static final TypeVisitor<Boolean, Void> noArgsVisitor
                                                  = new SimpleTypeVisitor6<Boolean, Void>() {
            @Override
            public Boolean visitExecutable(ExecutableType t, Void v) {
              return t.getParameterTypes().isEmpty();
            }
          };

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotations = new HashSet<>();
    annotations.add("darwin.annotations.*");
    return annotations;
  }

  @Override
  public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
    for (Element e : env.getElementsAnnotatedWith(ProviderGroup.class)) {
      ProviderGroup group = e.getAnnotation(ProviderGroup.class);
      if (group != null) {
        ServiceProvider sp = group.value();

        TypeElement te = (TypeElement) e;
        String type = getTypeName(sp);

        for (Element e2 : env.getElementsAnnotatedWith(te)) {
          boolean found = false;
          for (AnnotationMirror am : e2.getAnnotationMirrors()) {
            found |= am.getAnnotationType().equals(te.asType());
          }
          if (found) {
            appendProvider(sp.modus(), type, e2);
          }
        }
      }
    }

    for (Element e : env.getElementsAnnotatedWith(ServiceProvider.class)) {
      ServiceProvider sp = e.getAnnotation(ServiceProvider.class);
      if (sp != null) {
        appendProvider(sp.modus(), getTypeName(sp), e);
      }
    }

    return true;
  }

  private String getTypeName(ServiceProvider sp) {
    try {
      sp.value().getCanonicalName();
      throw new RuntimeException("should not reach this line, because of magixx");
    } catch (MirroredTypeException ex) {
      return ex.getTypeMirror().toString();
    }
  }

  private void appendProvider(CheckModus modus, String servicename, Element provider) {

    Messager m = processingEnv.getMessager();

    String providerName = getFQN(provider);

    if (modus == SAFE) {
      if (!hasNoArgsConstructor(provider)) {
        m.printMessage(ERROR, "Following ServiceProvider "
                              + "couldn't be registered, because no public NoArgs"
                              + " constructor is supplied. " + providerName);
        return;
      }
    }

    try {
      PrintWriter pw = getWriter(servicename);
      pw.println(providerName);
      pw.flush();
      m.printMessage(NOTE, "Registered provider \""
                           + providerName + "\" to service \"" + servicename + "\"");
    } catch (IOException ex) {
      Messager messager = processingEnv.getMessager();
      messager.printMessage(ERROR, ex.getLocalizedMessage());
      for (StackTraceElement ste : ex.getStackTrace()) {
        messager.printMessage(ERROR, ste.toString());
      }
    }
  }

  private String getFQN(Element e) {
    if (e instanceof TypeElement) {
      return ((TypeElement) e).getQualifiedName().toString();
    } else {
      return e.toString();
    }
  }

  private PrintWriter getWriter(String serviceName) throws IOException {
    String name = servicePath + serviceName;
    PrintWriter pw = writer.get(name);
    if (pw == null) {
      try {
        FileObject fo = processingEnv.getFiler().createResource(CLASS_OUTPUT, "", name);
        pw = new PrintWriter(fo.openWriter());
      } catch (FilerException ex) {
        processingEnv.getMessager().printMessage(NOTE, ex.getLocalizedMessage());
      }
      writer.put(name, pw);
    }
    return pw;
  }

  private boolean hasNoArgsConstructor(Element el) {
    for (Element subelement : el.getEnclosedElements()) {
      if (subelement.getKind() == CONSTRUCTOR
          && subelement.getModifiers().contains(PUBLIC)) {
        TypeMirror mirror = subelement.asType();
        if (mirror.accept(noArgsVisitor, null)) {
          return true;
        }
      }
    }
    return false;
  }
}
