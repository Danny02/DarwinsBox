package darwin.annotations;




import java.lang.annotation.*;

/**
 *
 * @author daniel
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface ServiceProvider
{
    Class value();
}
