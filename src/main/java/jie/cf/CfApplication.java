package jie.cf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import jie.cf.core.utils.CfUtils;

@SpringBootApplication
public class CfApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(CfApplication.class, args);
		// 持有Spring应用上下文
		CfUtils.setContext(context);
	}
}
