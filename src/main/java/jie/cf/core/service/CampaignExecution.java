package jie.cf.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jie.cf.core.dao.ICampaignCouponDAO;
import jie.cf.core.dao.ICampaignCouponItemDAO;
import jie.cf.core.dao.ICampaignDAO;
import jie.cf.core.dao.ICampaignEventDAO;
import jie.cf.core.dao.ICampaignResponseDAO;
import jie.cf.core.dao.ICampaignRuleDAO;
import jie.cf.core.dto.Campaign;
import jie.cf.core.dto.Campaign.CampaignStatus;
import jie.cf.core.dto.CampaignCoupon;
import jie.cf.core.dto.CampaignCouponItem;
import jie.cf.core.dto.CampaignCouponItem.CampaignCouponItemStatus;
import jie.cf.core.dto.CampaignEvent;
import jie.cf.core.dto.CampaignRequest;
import jie.cf.core.dto.CampaignResponse;
import jie.cf.core.dto.CampaignRule;
import jie.cf.core.utils.CfConstants;
import jie.cf.core.utils.CfUtils;
import jie.cf.core.utils.exception.CfException;

/**
 * 活动执行器
 * 
 * @author Jie
 *
 */
@Component
public class CampaignExecution {
	@Autowired
	private ICampaignDAO campaignDAO;
	@Autowired
	private ICampaignEventDAO campaignEventDAO;
	@Autowired
	private ICampaignRuleDAO campaignRuleDAO;
	@Autowired
	private ICampaignResponseDAO campaignResponseDAO;
	@Autowired
	private ICampaignCouponDAO campaignCouponDAO;
	@Autowired
	private ICampaignCouponItemDAO campaignCouponItemDAO;

	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 参加活动事件
	 * 
	 * @param campaignRequest
	 * @throws CfException
	 */
	public void campaignEvent(CampaignRequest campaignRequest) throws CfException {
		String campaignEventCode = campaignRequest.getCampaignEventCode();
		CfUtils.checkNotNull(campaignEventCode, "campaignEventCode can NOT be null");
		// 获取活动事件Code对应的活动事件对象
		CampaignEvent campaignEvent = campaignEventDAO.findOne(campaignEventCode);
		CfUtils.checkNotNull(campaignEvent, "campaignEventCode = " + campaignEventCode + " NOT found");

		// 校验活动是否在有效期内
		Campaign campaign = campaignDAO.findOne(campaignEvent.getCampaignId());
		checkCampaign(campaign);

		// 获取活动事件对象相关的活动规则列表
		String ruleType = StringUtils.isEmpty(campaignRequest.getRuleType()) ? CfConstants.PARTICIPATED
				: campaignRequest.getRuleType();
		List<CampaignRule> rules = campaignRuleDAO.findByParentId(campaignEvent.getId(), ruleType);
		if (CollectionUtils.isEmpty(rules))
			throw new CfException("campaignEventCode = " + campaignEventCode + " NOT found any campaignRules");
		// 执行活动规则
		for (CampaignRule rule : rules) {
			executeCampaignRule(rule, campaignRequest);
		}
	}

	/**
	 * 派发票券
	 * 
	 * @param campaignRequest
	 * @throws CfException
	 */
	public void distributeCampaignCoupon(CampaignRequest campaignRequest) throws CfException {
		// 解析并校验派发票券的请求
		Map<String, String> param = resolveCampaignRequest(campaignRequest, CfConstants.RESOLVE_TYPE_DISTRIBUTE);

		// 校验票券池是否在有效期内
		String campaignCouponCode = param.get(CfConstants.RESOLVE_campaignCouponCode);
		CampaignCoupon campaignCoupon = campaignCouponDAO.findOne(campaignCouponCode);
		checkCampaignCoupon(campaignCoupon);

		// 派发票券
		String campaignCouponItemStatus = StringUtils.isEmpty(param.get(CfConstants.RESOLVE_campaignCouponItemStatus))
				? CampaignCouponItemStatus.DISTRIBUTED.toString()
				: param.get(CfConstants.RESOLVE_campaignCouponItemStatus);
		Long campaignCouponItemAmount = StringUtils.isEmpty(param.get(CfConstants.RESOLVE_campaignCouponItemAmount))
				? 1L : Long.parseLong(param.get(CfConstants.RESOLVE_campaignCouponItemAmount));
		Date now = new Date();
		for (int i = 0; i != campaignCouponItemAmount.intValue(); ++i) {
			CampaignCouponItem tmp = new CampaignCouponItem(campaignCoupon);
			tmp.setSerialNumber(UUID.randomUUID().toString());
			tmp.setStatus(campaignCouponItemStatus);
			tmp.setStatusDate(now);
			tmp.setClientId(campaignRequest.getClientId());
			tmp.setMobile(campaignRequest.getMobile());
			tmp.setBindingDate(now);
			campaignCouponItemDAO.saveOne(tmp);
		}
	}

	/**
	 * 执行一条活动规则
	 * 
	 * @param campaignRule
	 * @param campaignRequest
	 * @throws CfException
	 */
	private void executeCampaignRule(CampaignRule campaignRule, CampaignRequest campaignRequest) throws CfException {
		// 校验活动规则是否在有效期内
		checkCampaignRule(campaignRule);
		// 构造Groovy脚本可用的参数
		Map<String, Object> param = param(campaignRequest);
		try {
			// 执行GroovyCondition
			Boolean condition = (Boolean) CfUtils.executeGroovyScript(campaignRule.getGroovyCondition(), param);
			if (condition) {
				CampaignResponse campaignResponse = new CampaignResponse(campaignRequest);
				campaignResponse.setCampaignEventId(campaignRule.getCampaignEventId());
				campaignResponse.setCampaignRuleId(campaignRule.getId());
				// Groovy脚本可用的业务数据
				param.put("campaignResponse", campaignResponse);

				CfUtils.executeGroovyScript(campaignRule.getGroovyBody(), param);

				campaignResponse.setStatusDate(new Date());
				campaignResponseDAO.saveOne(campaignResponse);
			}
		} catch (Exception e) {
			throw new CfException(e);
		}
	}

	/**
	 * 构造Groovy脚本可用的参数
	 * 
	 * @param campaignRequest
	 * @return
	 */
	private Map<String, Object> param(CampaignRequest campaignRequest) {
		Map<String, Object> ret = new HashMap<String, Object>();
		// Groovy脚本可用的bean
		ret.put("context", CfUtils.getContext());
		ret.put("campaignDAO", campaignDAO);
		ret.put("campaignEventDAO", campaignEventDAO);
		ret.put("campaignRuleDAO", campaignRuleDAO);
		ret.put("campaignCouponDAO", campaignCouponDAO);
		ret.put("campaignCouponItemDAO", campaignCouponItemDAO);
		ret.put("campaignExecution", this);
		// Groovy脚本可用的业务数据
		ret.put("campaignRequest", campaignRequest);
		ret.put("campaignEventCode", campaignRequest.getCampaignEventCode());
		ret.put("clientId", campaignRequest.getClientId());
		ret.put("mobile", campaignRequest.getMobile());
		ret.put("serialNumber", campaignRequest.getSerialNumber());
		return ret;
	}

	/**
	 * 解析并校验活动请求的reserved参数
	 * 
	 * @param campaignRequest
	 * @param type
	 * @return Map<String, String>
	 * @throws CfException
	 */
	private Map<String, String> resolveCampaignRequest(CampaignRequest campaignRequest, int type) throws CfException {
		try {
			String reserved = campaignRequest.getReserved();
			CfUtils.checkNotNull(reserved, "campaignRequest.reserved can NOT be null");
			Map<String, String> param = objectMapper.readValue(reserved, Map.class);
			if (MapUtils.isEmpty(param))
				throw new CfException("campaignRequest.reserved resolve failed");

			switch (type) {
			// 派发票券
			case CfConstants.RESOLVE_TYPE_DISTRIBUTE:
				String campaignCouponCode = param.get(CfConstants.RESOLVE_campaignCouponCode);
				CfUtils.checkNotNull(campaignCouponCode, "campaignCouponCode can NOT be null");
				break;
			// 微信“好友帮砍价”
			case CfConstants.RESOLVE_TYPE_WX:
				String targetOpenId = param.get(CfConstants.RESOLVE_targetOpenId);
				CfUtils.checkNotNull(targetOpenId, "targetOpenId can NOT be null");
				break;
			default:
				break;
			}
			return param;
		} catch (Exception e) {
			throw new CfException(e);
		}
	}

	/**
	 * 校验活动是否在有效期内
	 * 
	 * @param campaign
	 * @throws CfException
	 */
	private void checkCampaign(Campaign campaign) throws CfException {
		CfUtils.checkNotNull(campaign, "campaign can NOT be null");
		if (!StringUtils.equalsIgnoreCase(campaign.getStatus(), CampaignStatus.IN_PROCESS.toString()))
			throw new CfException("campaignCode = " + campaign.getCode() + " NOT IN_PROCESS");
		if (!CfUtils.hasNotExpired(campaign.getStartDate(), campaign.getEndDate()))
			throw new CfException("campaignCode = " + campaign.getCode() + " has expired, startDate = "
					+ campaign.getStartDate() + " endDate = " + campaign.getEndDate());
	}

	/**
	 * 校验活动规则是否在有效期内
	 * 
	 * @param campaignRule
	 * @throws CfException
	 */
	private void checkCampaignRule(CampaignRule campaignRule) throws CfException {
		CfUtils.checkNotNull(campaignRule, "campaignRule can NOT be null");
		if (!CfUtils.hasNotExpired(campaignRule.getStartDate(), campaignRule.getEndDate()))
			throw new CfException("campaignRuleId = " + campaignRule.getId() + " has expired, startDate = "
					+ campaignRule.getStartDate() + " endDate = " + campaignRule.getEndDate());
	}

	/**
	 * 校验票券池是否在有效期内
	 * 
	 * @param campaignCoupon
	 * @throws CfException
	 */
	private void checkCampaignCoupon(CampaignCoupon campaignCoupon) throws CfException {
		CfUtils.checkNotNull(campaignCoupon, "campaignCoupon can NOT be null");
		if (!CfUtils.hasNotExpired(campaignCoupon.getStartDate(), campaignCoupon.getEndDate()))
			throw new CfException("campaignCouponCode = " + campaignCoupon.getCode() + " has expired, startDate = "
					+ campaignCoupon.getStartDate() + " endDate = " + campaignCoupon.getEndDate());
	}

	/**
	 * 统计活动事件的响应次数
	 * 
	 * @param campaignRequest
	 * @return
	 * @throws CfException
	 */
	public long countCampaignResponse(CampaignRequest campaignRequest) throws CfException {
		Long campaignEventId = campaignEventDAO.findId(campaignRequest.getCampaignEventCode());
		List<CampaignResponse> lst = campaignResponseDAO.findByCampaignRequest(campaignEventId,
				campaignRequest.getClientId(), campaignRequest.getMobile());
		if (CollectionUtils.isEmpty(lst))
			return 0;
		else
			return lst.size();
	}

	/**
	 * 绑定票券
	 * 
	 * @param campaignCouponItem
	 * @param campaignRequest
	 * @throws CfException
	 */
	public void bindCampaignCoupon(CampaignCouponItem campaignCouponItem, CampaignRequest campaignRequest)
			throws CfException {
		CfUtils.checkNotNull(campaignCouponItem, "campaignCouponItem can NOT be null");
		Date now = new Date();
		campaignCouponItem.setStatus(convertRuleType2CouponItemStatus(campaignRequest.getRuleType()));
		campaignCouponItem.setStatusDate(now);
		campaignCouponItem.setClientId(campaignRequest.getClientId());
		campaignCouponItem.setMobile(campaignRequest.getMobile());
		campaignCouponItem.setOpenId(campaignRequest.getOpenId());
		campaignCouponItem.setBindingDate(now);
		campaignCouponItem.setStartDate(now);
		campaignCouponItem.setEndDate(DateUtils.addDays(now, CfConstants.CampainCouponExpireDays));
		campaignCouponItemDAO.saveOne(campaignCouponItem);
	}

	/**
	 * 是否已参加过微信“好友帮砍价”活动
	 * 
	 * @param campaignRequest
	 * @return boolean
	 * @throws CfException
	 */
	public boolean hasParticipatedWxCampaign(CampaignRequest campaignRequest) throws CfException {
		Long campaignEventId = campaignEventDAO.findId(campaignRequest.getCampaignEventCode());
		String targetOpenId = null;

		if (StringUtils.equalsIgnoreCase(campaignRequest.getRuleType(), CfConstants.WX_INITIATE)) {
			// 发起人
		} else if (StringUtils.equalsIgnoreCase(campaignRequest.getRuleType(), CfConstants.WX_PARTICIPATE)) {
			// 参与人
			Map<String, String> param = resolveCampaignRequest(campaignRequest, CfConstants.RESOLVE_TYPE_WX);
			targetOpenId = param.get(CfConstants.RESOLVE_targetOpenId);
			// 校验目标OpenId是否已发起
			if (CollectionUtils.isEmpty(campaignResponseDAO.findByWxOpenId(campaignEventId, targetOpenId, null)))
				throw new CfException("targetOpenId = " + targetOpenId + " NOT available");
		}

		List<CampaignResponse> lst = campaignResponseDAO.findByWxOpenId(campaignEventId, campaignRequest.getOpenId(),
				targetOpenId);
		if (CollectionUtils.isEmpty(lst))
			return false;
		else
			return true;
	}

	/**
	 * CampaignRuleType映射到CampaignCouponItemStatus
	 * 
	 * @param campaignRuleType
	 * @return CampaignCouponItemStatus
	 * @throws CfException
	 */
	private String convertRuleType2CouponItemStatus(String campaignRuleType) throws CfException {
		String ret = null;
		if (StringUtils.equalsIgnoreCase(CfConstants.DISTRIBUTED, campaignRuleType)
				|| StringUtils.equalsIgnoreCase(CfConstants.USED, campaignRuleType))
			ret = campaignRuleType;
		else {
			if (StringUtils.equalsIgnoreCase(CfConstants.EXCHANGE, campaignRuleType))
				ret = CampaignCouponItemStatus.USED.toString();
			else
				throw new CfException("WRONG campaignRuleType: " + campaignRuleType);
		}
		return ret;
	}
}
