package darwin.tools.annotations;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.*;

/**
 *
 * @author daniel
 * <p/>
 * Usage with maven: add this to the pom
 * <p/>
 * <build> <plugins> <!-- Run annotation processors on src/main/java sources -->
 * <plugin> <groupId>org.bsc.maven</groupId>
 * <artifactId>maven-processor-plugin</artifactId> <executions> <execution>
 * <id>process</id> <goals> <goal>process</goal> </goals>
 * <phase>generate-sources</phase> <configuration> <processors>
 * <processor>darwin.tools.annotations.ServiceProcessor</processor>
 * </processors> </configuration> </execution> </executions> </plugin> <!--
 * Disable annotation processors during normal compilation --> <plugin>
 * <groupId>org.apache.maven.plugins</groupId>
 * <artifactId>maven-compiler-plugin</artifactId> <configuration>
 * <compilerArgument>-proc:none</compilerArgument> <source>1.7</source>
 * <target>1.7</target> </configuration> </plugin>
 * <p/>
 * </plugins> </build>
 * <p/>
 * <pluginRepositories> <pluginRepository> <id>sonatype-repo</id>
 * <url>https://oss.sonatype.org/content/repositories/snapshots</url> <releases>
 * <enabled>false</enabled> </releases> <snapshots> <enabled>true</enabled>
 * </snapshots> </pluginRepository> </pluginRepositories>
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ServiceProcessor extends AbstractProcessor
{

    private static final String servicePath = "META-INF/services/";
    private static final Map<String, PrintWriter> writer = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> annotations = new HashSet<>();
        annotations.add(ServiceProvider.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env)
    {
        for (Element e : env.getElementsAnnotatedWith(ServiceProvider.class)) {
            ServiceProvider sp = e.getAnnotation(ServiceProvider.class);
            appendProvider(sp, e);
        }
        return true;
    }

    private void appendProvider(ServiceProvider provider, Element service)
    {
        assert provider != null;

        String servicename = null;
        try {
            provider.value().getCanonicalName();
        } catch (MirroredTypeException exsx) {
            servicename = exsx.getTypeMirror().toString();
        }
        try {
            PrintWriter pw = getWriter(servicename);
            pw.println(getFQN(service));
            pw.flush();
        } catch (IOException ex) {
            Messager messager = processingEnv.getMessager();
            messager.printMessage(Diagnostic.Kind.ERROR, ex.getLocalizedMessage());
            for (StackTraceElement ste : ex.getStackTrace()) {
                messager.printMessage(Diagnostic.Kind.ERROR, ste.toString());
            }
        }
    }

    private Name getFQN(Element e)
    {
        return ((TypeElement) e).getQualifiedName();
    }

    private PrintWriter getWriter(String serviceName) throws IOException
    {
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
}
