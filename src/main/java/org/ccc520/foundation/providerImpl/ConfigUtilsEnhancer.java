package org.ccc520.foundation.providerImpl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.ctrip.framework.foundation.internals.io.BOMInputStream;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;

/**** 
 * @ClassName ConfigUtilsEnhancer  
 * @Description 修改配置文件缓存位置  
 * @author ccc520  
 * @date 2018年6月17日 下午4:51:00  
 * @modificationHistory===============逻辑或功能性重大变更记录
 * @modify by user：  (修改人)
 * @modify by reason：  (修改原因) 
 */
public class ConfigUtilsEnhancer implements ApplicationContextInitializer<ConfigurableApplicationContext>{

	static boolean enhanced = false;
	
	public void enhance() {
		if(!enhanced) {
			try {
				/*ClassPool cp = ClassPool.getDefault();
				CtClass cc = cp.get("com.ctrip.framework.apollo.util.ConfigUtil");
				
				CtMethod cm = cc.getDeclaredMethod("getAppId");
				cm.setBody("{String appId = com.ctrip.framework.foundation.Foundation.app().getAppId();\r\n" + 
						"    if (org.apache.commons.lang3.StringUtils.isBlank(appId)) {\r\n" + 
						"      appId = com.ctrip.framework.apollo.core.ConfigConsts.NO_APPID_PLACEHOLDER;\r\n" + 
						"      logger.warn(\"app.id(spring.application.name) is not set, please make sure it is set in classpath:/application.properties, now apollo \" +\r\n" + 
						"          \"will only load public namespace configurations!\");\r\n" + 
						"    }\r\n" + 
						"    return appId;}");
				
				cm = cc.getDeclaredMethod("getApolloEnv");
				cm.setBody("{com.ctrip.framework.apollo.core.enums.Env env = com.ctrip.framework.apollo.core.enums.EnvUtils.transformEnv(com.ctrip.framework.foundation.Foundation.server().getEnvType());\r\n" + 
						"    if (env == null) {\r\n" + 
						"      String message = \"env(apollo.env) is not set, please make sure it is set in classpath:/application.properties !\";"+
						"      logger.error(message);\r\n" + 
						"      throw new com.ctrip.framework.apollo.exceptions.ApolloConfigException(message);\r\n" + 
						"    }\r\n" + 
						"    return env;}");
				
				//本地缓存路径
				CtField f = CtField.make("private String cacheRoot;", cc);
				cc.addField(f);
				
				//从配置文件(application.properties)读取apollo.cacheRoot
				CtMethod m = CtNewMethod.make("private void initCacheRoot(){"
											+ "    try {\r\n" + 
											"	       java.io.InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(\"/application.properties\");\r\n" + 
											"	       if (in == null) {\r\n" + 
											"	           in = com.ctrip.framework.apollo.util.ConfigUtil.class.getResourceAsStream(\"/application.properties\");\r\n" + 
											"	       }\r\n" + 
											
											"		   if (in == null) {\r\n" + 
											"		       logger.warn(\"{} not found from classpath!\", \"/application.properties\");\r\n" + 
											"		   }\r\n" + 
											"          java.util.Properties props = new java.util.Properties(); \r\n"+
											"		   props.load(new java.io.InputStreamReader(new com.ctrip.framework.foundation.internals.io.BOMInputStream(in), java.nio.charset.StandardCharsets.UTF_8));\r\n" + 
											"          cacheRoot = props.getProperty(\"apollo.cacheRoot\"); \r\n"+
											"      } catch (Throwable ex) {\r\n" + 
											"		   logger.error(\"Initialize DefaultApplicationProvider failed.\", ex);\r\n" + 
											"      }"+
											"  };		",
						cc);
				cc.addMethod(m);
				
				m = cc.getDeclaredMethod("getDefaultLocalCacheDir");
				m.setBody("{return new String(cacheRoot + getAppId());}");
				
				CtConstructor[] ctcs = cc.getConstructors();
				for(CtConstructor ctc : ctcs) {
					ctc.insertAfter("initCacheRoot();");
				}
				
				//重新加载到ClassLoader中
				cc.toClass();
				
				cc.detach();*/
			} catch (Exception e) {
				e.printStackTrace();
			}
			enhanced = true;
		}
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		enhance();
	}
}
