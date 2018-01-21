package jie.cf.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jie.cf.core.utils.CfUtils;

/**
 * 控制台 - 执行Groovy脚本
 * 
 * @author Jie
 *
 */
@Controller
@RequestMapping("/")
public class HomeController {
	@RequestMapping(method = RequestMethod.GET)
	public String home() {
		return "home";
	}

	/**
	 * 执行Groovy脚本
	 * 
	 * @param script
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/eval", method = RequestMethod.GET)
	public @ResponseBody String eval(@RequestParam(value = "script") String script) throws Exception {
		Object obj = CfUtils.executeGroovyScript(script, param());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ret", obj);
		return jsonObject.toString();
	}

	/** 构造Groovy脚本可用的参数 */
	private Map<String, Object> param() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("context", CfUtils.getContext()); // Spring应用上下文
		return ret;
	}
}
