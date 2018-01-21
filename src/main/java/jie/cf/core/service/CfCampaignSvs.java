package jie.cf.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jie.cf.core.api.ICfCampaignSvs;
import jie.cf.core.dto.CampaignRequest;
import jie.cf.core.utils.CfConstants;
import jie.cf.core.utils.CfUtils;
import jie.cf.core.utils.exception.CfException;

@Component
public class CfCampaignSvs implements ICfCampaignSvs {
	@Autowired
	private CampaignExecution campaignExecution;

	@Override
	public void campaignEvent(CampaignRequest campaignRequest) throws CfException {
		// 校验参加活动事件的请求
		checkCampaignRequest(campaignRequest);
		campaignExecution.campaignEvent(campaignRequest);
	}

	@Override
	public void distribute(CampaignRequest campaignRequest) throws CfException {
		// 校验兑换票券的请求
		checkCampaignRequest(campaignRequest);
		campaignExecution.distributeCampaignCoupon(campaignRequest);
	}

	@Override
	public void use(CampaignRequest campaignRequest) throws CfException {
		// 校验使用票券的请求
		String serialNumber = campaignRequest.getSerialNumber();
		CfUtils.checkNotNull(serialNumber, "serialNumber can NOT be NULL");
		// 参加使用票券的活动事件
		campaignRequest.setRuleType(CfConstants.USED);
		campaignEvent(campaignRequest);
	}

	@Override
	public void exchange(CampaignRequest campaignRequest) throws CfException {
		// 校验兑换票券的请求
		String serialNumber = campaignRequest.getSerialNumber();
		CfUtils.checkNotNull(serialNumber, "serialNumber can NOT be NULL");
		// 参加兑换票券的活动事件
		campaignRequest.setRuleType(CfConstants.EXCHANGE);
		campaignEvent(campaignRequest);
	}

	@Override
	public void wechatCampaignEvent(CampaignRequest campaignRequest) throws CfException {
		// 校验参加微信“好友帮砍价”活动事件的请求
		CfUtils.checkNotNull(campaignRequest.getOpenId(), "wechat openId can NOT be null");
		String campaignRuleType = campaignRequest.getRuleType();
		if (!StringUtils.equalsIgnoreCase(CfConstants.WX_INITIATE, campaignRuleType)
				&& !StringUtils.equalsIgnoreCase(CfConstants.WX_PARTICIPATE, campaignRuleType))
			throw new CfException("WRONG campaignRuleType: " + campaignRuleType);
		// 参加微信“好友帮砍价”活动事件
		campaignEvent(campaignRequest);
	}

	/**
	 * 校验活动请求
	 * 
	 * @param campaignRequest
	 * @throws CfException
	 */
	private void checkCampaignRequest(CampaignRequest campaignRequest) throws CfException {
		CfUtils.checkNotNull(campaignRequest, "campaignRequest can NOT be null");
		if ((campaignRequest.getClientId() == null) && StringUtils.isEmpty(campaignRequest.getMobile())
				&& StringUtils.isEmpty(campaignRequest.getOpenId()))
			throw new CfException("clientId, mobile, openId can NOT be ALL null");
	}
}
