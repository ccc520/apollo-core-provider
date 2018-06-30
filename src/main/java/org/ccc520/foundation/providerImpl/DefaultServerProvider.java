package org.ccc520.foundation.providerImpl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.framework.foundation.internals.io.BOMInputStream;
import com.ctrip.framework.foundation.spi.provider.Provider;
import com.ctrip.framework.foundation.spi.provider.ServerProvider;

/**** 
 * @ClassName DefaultApplicationProvider  
 * @Description 改从application.properties读取配置文件apollo.env取代env,apollo.idc取代idc
 * @author ccc520  
 * @date 2018年6月17日 上午10:17:12  
 * @modificationHistory===============逻辑或功能性重大变更记录
 * @modify by user：  (修改人)
 * @modify by reason：  (修改原因) 
 */
public class DefaultServerProvider implements ServerProvider {
  private static final Logger logger = LoggerFactory.getLogger(DefaultServerProvider.class);
  public static final String APP_PROPERTIES_CLASSPATH = "/application.properties";

  private String m_env;
  private String m_dc;

  private Properties m_serverProperties = new Properties();

  @Override
  public void initialize() {
      try {
          InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_PROPERTIES_CLASSPATH);
	      if (in == null) {
	          in = DefaultApplicationProvider.class.getResourceAsStream(APP_PROPERTIES_CLASSPATH);
	      }

		  if (in == null) {
		      logger.warn("{} not found from classpath!", APP_PROPERTIES_CLASSPATH);
		  }
          initialize(in);
	  } catch (Throwable ex) {
		  logger.error("Initialize DefaultApplicationProvider failed.", ex);
	  }		
  }

  @Override
  public void initialize(InputStream in) {
    try {
      if (in != null) {
        try {
          m_serverProperties.load(new InputStreamReader(new BOMInputStream(in), StandardCharsets.UTF_8));
        } finally {
          in.close();
        }
      }

      initEnvType();
      initDataCenter();
    } catch (Throwable ex) {
      logger.error("Initialize DefaultServerProvider failed.", ex);
    }
  }

  @Override
  public String getDataCenter() {
    return m_dc;
  }

  @Override
  public boolean isDataCenterSet() {
    return m_dc != null;
  }

  @Override
  public String getEnvType() {
    return m_env;
  }

  @Override
  public boolean isEnvTypeSet() {
    return m_env != null;
  }

  @Override
  public String getProperty(String name, String defaultValue) {
    if ("env".equalsIgnoreCase(name)) {
      String val = getEnvType();
      return val == null ? defaultValue : val;
    } else if ("dc".equalsIgnoreCase(name)) {
      String val = getDataCenter();
      return val == null ? defaultValue : val;
    } else {
      String val = m_serverProperties.getProperty(name, defaultValue);
      return val == null ? defaultValue : val.trim();
    }
  }

  @Override
  public Class<? extends Provider> getType() {
    return ServerProvider.class;
  }

  private void initEnvType() {
    // 3. Try to get environment from file "application.properties"
    m_env = m_serverProperties.getProperty("apollo.env");
    if (StringUtils.isNotBlank(m_env)) {
      m_env = m_env.trim();
      logger.info("Environment is set to [{}] by property 'env' in server.properties.", m_env);
      return;
    }

    // 4. Set environment to null.
    m_env = null;
    logger.warn("Environment is set to null. Because it is not available in property 'env' from the properties InputStream.");
  }

  private void initDataCenter() {
    // 3. Try to get idc from from file "server.properties"
    m_dc = m_serverProperties.getProperty("apollo.idc");
    if (StringUtils.isNotBlank(m_dc)) {
      m_dc = m_dc.trim();
      logger.info("Data Center is set to [{}] by property 'idc' in server.properties.", m_dc);
      return;
    }

    // 4. Set Data Center to null.
    m_dc = null;
    logger.debug("Data Center is set to null. Because it is not available in either (1) JVM system property 'idc', (2) OS env variable 'IDC' nor (3) property 'idc' from the properties InputStream.");
  }

  @Override
  public String toString() {
    return "environment [" + getEnvType() + "] data center [" + getDataCenter() + "] properties: " + m_serverProperties
        + " (DefaultServerProvider)";
  }
}
