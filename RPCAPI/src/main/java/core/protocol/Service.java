package core.protocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xinszhou on 23/03/2017.
 */

public interface Service extends Comparable<Service> {
    String getName();

    Object getDelegatedBean(String className);
}
