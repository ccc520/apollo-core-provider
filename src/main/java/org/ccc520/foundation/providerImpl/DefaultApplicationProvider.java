package org.ccc520.foundation.providerImpl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.framework.foundation.internals.io.BOMInputStream;
import com.ctrip.framework.foundation.spi.provider.ApplicationProvider;
import com.ctrip.framework.foundation.spi.provider.Provider;

/**** 
 * @ClassName DefaultApplicationProvider  
 * @Description 改从application.properties读取配置文件spring.application.name取代app.id
 * @author ccc520  
 * @date 2018年6月17日 上午10:06:19  
 * @modificationHistory===============逻辑或功能性重大变更记录
 * @modify by user：  (修改人)
 * @modify by reason：  (修改原因) 
 */
public class DefaultApplicationProvider implements ApplicationProvider{

	private static final Logger logger = LoggerFactory.getLogger(DefaultApplicationProvider.class);
	public static final String APP_PROPERTIES_CLASSPATH = "/application.properties";
	private Properties m_appProperties = new Properties();
	
	private String m_appId;
	
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
	                m_appProperties.load(new InputStreamReader(new BOMInputStream(in), StandardCharsets.UTF_8));
	            } finally {
	                in.close();
	            }
	        }
	    initAppId();
	    } catch (Throwable ex) {
	      logger.error("Initialize DefaultApplicationProvider failed.", ex);
	    }		
	}
	
	@Override
	public Class<? extends Provider> getType() {
		return ApplicationProvider.class;
	}

	@Override
	public String getProperty(String name, String defaultValue) {
		if ("app.id".equals(name)) {
		    String val = getAppId();
		    return val == null ? defaultValue : val;
		} else {
		    String val = m_appProperties.getProperty(name, defaultValue);
		    return val == null ? defaultValue : val;
		}
	}

	@Override
	public String getAppId() {
	    return m_appId;
	}

	@Override
	public boolean isAppIdSet() {
		return StringUtils.isNotBlank(m_appId);
	}
	
	private void initAppId() {
        // 2. Try to get app id from application.properties.
        m_appId = m_appProperties.getProperty("spring.application.name");
        if (StringUtils.isNotBlank(m_appId)) {
            m_appId = m_appId.trim();
            logger.info("App ID is set to {} by app.id property from {}", m_appId, APP_PROPERTIES_CLASSPATH);
            return;
        }

        m_appId = null;
        logger.warn("app.id is not available from System Property and {}. It is set to null", APP_PROPERTIES_CLASSPATH);
    }

    @Override
    public String toString() {
        return "appId [" + getAppId() + "] properties: " + m_appProperties + " (DefaultApplicationProvider)";
    }

}
