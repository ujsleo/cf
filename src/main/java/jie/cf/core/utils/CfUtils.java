package jie.cf.core.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.ApplicationContext;

import jie.cf.core.utils.exception.CfException;

public class CfUtils {
	/** 持有Spring应用上下文 */
	private static ApplicationContext context;
	private static ScriptEngine engine = null;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static ApplicationContext getContext() {
		return context;
	}

	public static void setContext(ApplicationContext context) {
		CfUtils.context = context;
	}

	/**
	 * 执行Groovy脚本
	 * 
	 * @param script
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static Object executeGroovyScript(String script, Map<String, Object> param) throws Exception {
		checkNotNull(script, "Groovy script can NOT be null");
		// Groovy脚本import包 + 执行脚本
		String evalScript = CfConstants.GROOVY_IMPORTS + script;

		ScriptEngine engine = groovyScriptEngine();
		Bindings bindings = engine.createBindings();
		bindings.putAll(param);
		return engine.eval(evalScript, bindings);
	}

	/**
	 * 获取ScriptEngine
	 * 
	 * @return ScriptEngine
	 */
	private static ScriptEngine groovyScriptEngine() {
		if (engine == null) {
			ScriptEngineManager engineManager = new ScriptEngineManager();
			engine = engineManager.getEngineByName("groovy");
		}
		return engine;
	}

	/**
	 * checkNotNull
	 * 
	 * @param reference
	 * @param errorMessage
	 * @throws CfException
	 */
	public static <T> T checkNotNull(T reference, String errorMessage) throws CfException {
		if (reference == null)
			throw new CfException(errorMessage);
		return reference;
	}

	public static Date convert2Date(Object rhs) throws Exception {
		if (rhs == null)
			return null;
		return sdf.parse(rhs.toString());
	}

	/**
	 * 判断是否在指定有效期内
	 * 
	 * @param startDate
	 * @param endDate
	 * @return false - 已过期
	 */
	public static boolean hasNotExpired(Date startDate, Date endDate) {
		Date now = new Date();
		return (DateUtils.truncatedCompareTo(startDate, now, Calendar.SECOND) < 0)
				&& (DateUtils.truncatedCompareTo(now, endDate, Calendar.SECOND) < 0);
	}
}
