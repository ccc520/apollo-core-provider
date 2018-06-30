package org.ccc520.foundation.providerImpl;

import com.ctrip.framework.foundation.spi.ProviderManager;

public class NullProviderManager implements ProviderManager {
  public static final NullProvider provider = new NullProvider();

  @Override
  public String getProperty(String name, String defaultValue) {
    return defaultValue;
  }

  @Override
  public NullProvider provider(Class clazz) {
    return provider;
  }

  @Override
  public String toString() {
    return provider.toString();
  }
}
