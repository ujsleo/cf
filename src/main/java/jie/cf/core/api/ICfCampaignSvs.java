package jie.cf.core.api;

import jie.cf.core.dto.CampaignRequest;
import jie.cf.core.utils.exception.CfException;

/**
 * Campaign service for the CF
 * 
 * @author Jie
 *
 */
public interface ICfCampaignSvs {
	/**
	 * 参加活动事件
	 * 
	 * @param campaignRequest
	 *            活动请求
	 * @throws CfException
	 */
	void campaignEvent(CampaignRequest campaignRequest) throws CfException;

	/**
	 * 派发票券
	 * 
	 * @param campaignRequest
	 * @throws CfException
	 */
	void distribute(CampaignRequest campaignRequest) throws CfException;

	/**
	 * 使用票券
	 * 
	 * @param campaignRequest
	 * @throws CfException
	 */
	void use(CampaignRequest campaignRequest) throws CfException;

	/**
	 * 兑换票券（凭兑换码换票券）
	 * 
	 * @param campaignRequest
	 * @throws CfException
	 */
	void exchange(CampaignRequest campaignRequest) throws CfException;

	/**
	 * 参加微信“好友帮砍价”活动事件
	 * 
	 * @param campaignRequest
	 * @throws CfException
	 */
	void wechatCampaignEvent(CampaignRequest campaignRequest) throws CfException;
}
