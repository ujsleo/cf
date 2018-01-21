package jie.cf.core.utils.exception;

/**
 * CF异常
 * 
 * @author Jie
 *
 */
public class CfException extends Exception {
	public CfException(String msg) {
		super(msg);
	}

	public CfException(Throwable e) {
		super(e);
	}
}
