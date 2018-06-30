package org.ccc520.foundation.providerImpl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.framework.foundation.spi.ProviderManager;
import com.ctrip.framework.foundation.spi.provider.Provider;

/****
 * @ClassName: DefaultProviderManager
 * @Description: 实现自定义SPI取代apollo-core实现
 * @author ccc520
 * @date 2018年6月17日 上午10:40:56
 * @modificationHistory===============逻辑或功能性重大变更记录
 * @modify by user： (修改人)
 * @modify by reason： (修改原因)
 */
public class DefaultProviderManager implements ProviderManager {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProviderManager.class);

	private Map<Class<? extends Provider>, Provider> m_providers = new LinkedHashMap<Class<? extends Provider>, Provider>();

	public DefaultProviderManager() {
		
		// Load per-application configuration, like app id, from
		// classpath:/application.properties
		Provider applicationProvider = new DefaultApplicationProvider();
		applicationProvider.initialize();
		register(applicationProvider);

		// Load network parameters
		Provider networkProvider = new DefaultNetworkProvider();
		networkProvider.initialize();
		register(networkProvider);

		// Load environment (fat, fws, uat, prod ...) and dc, from
		// classpath:/application.properties
		// environment variables.
		Provider serverProvider = new DefaultServerProvider();
		serverProvider.initialize();
		register(serverProvider);
	}

	public synchronized void register(Provider provider) {
		m_providers.put(provider.getType(), provider);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Provider> T provider(Class<T> clazz) {
		Provider provider = m_providers.get(clazz);

		if (provider != null) {
			return (T) provider;
		} else {
			logger.error(
					"No provider [{}] found in DefaultProviderManager, please make sure it is registered in DefaultProviderManager ",
					clazz.getName());
			return (T) NullProviderManager.provider;
		}
	}

	@Override
	public String getProperty(String name, String defaultValue) {
		for (Provider provider : m_providers.values()) {
			String value = provider.getProperty(name, null);

			if (value != null) {
				return value;
			}
		}

		return defaultValue;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(512);
		if (null != m_providers) {
			for (Map.Entry<Class<? extends Provider>, Provider> entry : m_providers.entrySet()) {
				sb.append(entry.getValue()).append("\n");
			}
		}
		sb.append("(DefaultProviderManager)").append("\n");
		return sb.toString();
	}
}
