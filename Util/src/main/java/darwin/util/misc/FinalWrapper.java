/*
 * Copyright (C) 2014 Daniel Heinrich <Daniel.Heinrich@procon-it.de>
 */
package darwin.util.misc;

/**
 * A helper class to help with correct lazy initialisation of Fields.
 *
 * The following is the only correct way of doing correct double locked
 * synchronized lazy initialization on the Java VM.
 *
 * <pre>
 * {@code
 * class Foo
 *  {
 *      private FinalWrapper<DBConnection> connection;
 *
 *      public DBConnection getConnection()
 *      {
 *          FinalWrapper<Map<String, String>> wrapper = propertyWrapper;
 *
 *          if (wrapper == null) {
 *              synchronized (this) {
 *                  if (propertyWrapper == null) {
 *                      propertyWrapper = new FinalWrapper<>(initPropertyValues());
 *                  }
 *                  wrapper = propertyWrapper;
 *              }
 *          }
 *
 *          return wrapper.value;
 *      }
 * }
 * }
 * </pre>
 *
 * @author Daniel Heinrich <Daniel.Heinrich@procon-it.de>
 */
public class FinalWrapper<T> {

    public final T value;

    public FinalWrapper(T value) {
        this.value = value;
    }
}