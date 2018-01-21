package jie.cf.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import jie.cf.core.api.ICfCampaignSvs;
import jie.cf.core.dao.ICampaignCouponDAO;
import jie.cf.core.dao.ICampaignCouponItemDAO;
import jie.cf.core.dao.ICampaignDAO;
import jie.cf.core.dao.ICampaignEventDAO;
import jie.cf.core.dao.ICampaignResponseDAO;
import jie.cf.core.dao.ICampaignRuleDAO;
import jie.cf.core.dto.Campaign;
import jie.cf.core.dto.Campaign.CampaignStatus;
import jie.cf.core.dto.CampaignCoupon;
import jie.cf.core.dto.CampaignCoupon.CampaignCouponType;
import jie.cf.core.dto.CampaignCouponItem;
import jie.cf.core.dto.CampaignCouponItem.CampaignCouponItemStatus;
import jie.cf.core.dto.CampaignEvent;
import jie.cf.core.dto.CampaignRequest;
import jie.cf.core.dto.CampaignResponse;
import jie.cf.core.dto.CampaignRule;
import jie.cf.core.utils.CfConstants;
import jie.cf.core.utils.exception.CfException;
import jie.cf.core.utils.stereotype.CfDemo;

@CfDemo
public class Demo {
	@Autowired
	private ICampaignDAO campaignDAO;
	@Autowired
	private ICampaignEventDAO campaignEventDAO;
	@Autowired
	private ICampaignRuleDAO campaignRuleDAO;
	@Autowired
	private ICampaignCouponDAO campaignCouponDAO;
	@Autowired
	private ICampaignCouponItemDAO campaignCouponItemDAO;
	@Autowired
	private ICampaignResponseDAO campaignResponseDAO;
	@Autowired
	private ICfCampaignSvs campaignSvs;

	/** 参加活动 */
	public List<CampaignResponse> campaign() throws CfException {
		CampaignRequest campaignRequest = createCampaignRequest();
		campaignSvs.campaignEvent(campaignRequest);
		return response(campaignRequest);
	}

	/** 派发票券 */
	public List<CampaignCouponItem> distribute() throws CfException, JSONException {
		CampaignRequest campaignRequest = createCampaignRequest();
		// reserved参数
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(CfConstants.RESOLVE_campaignCouponCode, "COUPON");
		jsonObject.put(CfConstants.RESOLVE_campaignCouponItemAmount, "2");
		campaignRequest.setReserved(jsonObject.toString());
		campaignSvs.distribute(campaignRequest);
		return campaignCouponItemDAO.findByUser(campaignRequest.getClientId(), campaignRequest.getMobile());
	}

	/** 使用票券 */
	public List<CampaignResponse> use() throws CfException {
		CampaignRequest campaignRequest = createCampaignRequest();
		// 使用DISTRIBUTED票券
		for (CampaignCouponItem coupon : campaignCouponItemDAO.findByUser(campaignRequest.getClientId(),
				campaignRequest.getMobile()))
			if (StringUtils.equals(CampaignCouponItemStatus.DISTRIBUTED.toString(), coupon.getStatus()))
				campaignRequest.setSerialNumber(coupon.getSerialNumber());
		campaignSvs.use(campaignRequest);
		return response(campaignRequest);
	}

	/** 兑换票券 */
	public List<CampaignResponse> exchange() throws CfException, JSONException {
		CampaignRequest campaignRequest = createCampaignRequest();
		// 使用AVAILABLE票券
		CampaignCouponItem coupon = campaignCouponItemDAO
				.findOneAvailable(campaignEventDAO.findId(campaignRequest.getCampaignEventCode()));
		campaignRequest.setSerialNumber(coupon.getSerialNumber());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(CfConstants.RESOLVE_campaignCouponCode, "COUPON");
		jsonObject.put(CfConstants.RESOLVE_campaignCouponItemAmount, "2");
		campaignRequest.setReserved(jsonObject.toString());
		campaignSvs.exchange(campaignRequest);
		return response(campaignRequest);
	}

	/** 微信“好友帮砍价”- 发起 */
	public List<CampaignResponse> wechatCampaignInitiate() throws CfException {
		CampaignRequest campaignRequest = createCampaignRequest();
		campaignRequest.setRuleType(CfConstants.WX_INITIATE);
		campaignRequest.setOpenId("wx10086");
		campaignSvs.wechatCampaignEvent(campaignRequest);
		return response(campaignRequest);
	}

	/** 微信“好友帮砍价”- 参与 */
	public List<CampaignResponse> wechatCampaignParticipate() throws CfException, JSONException {
		CampaignRequest campaignRequest = createCampaignRequest();
		campaignRequest.setRuleType(CfConstants.WX_PARTICIPATE);
		campaignRequest.setOpenId("wx10010");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(CfConstants.RESOLVE_targetOpenId, "wx10086");
		campaignRequest.setReserved(jsonObject.toString());
		campaignSvs.wechatCampaignEvent(campaignRequest);
		return response(campaignRequest);
	}

	private List<CampaignResponse> response(CampaignRequest campaignRequest) {
		return campaignResponseDAO.findByCampaignRequest(
				campaignEventDAO.findId(campaignRequest.getCampaignEventCode()), campaignRequest.getClientId(),
				campaignRequest.getMobile());
	}

	/** 活动请求数据准备 */
	public static CampaignRequest createCampaignRequest() {
		CampaignRequest ret = new CampaignRequest();
		ret.setCampaignEventCode("CampaignEventCode");
		ret.setClientId(10086L);
		ret.setMobile("13888888888");
		return ret;
	}

	/** 测试数据准备，写入数据库 */
	public void createData() {
		// 活动数据
		Campaign campaign = createCampaign();
		campaignDAO.saveOne(campaign);
		Long campaignId = campaignDAO.findId(campaign.getCode());
		// 活动事件数据
		CampaignEvent campaignEvent = createCampaignEvent(campaignId);
		campaignEventDAO.saveOne(campaignEvent);
		Long campaignEventId = campaignEventDAO.findId(campaignEvent.getCode());
		// 活动规则数据
		CampaignRule campaignRuleParticipated = createCampaignRuleParticipated(campaignEventId);
		campaignRuleDAO.saveOne(campaignRuleParticipated);
		CampaignRule campaignRuleUse = createCampaignRuleUse(campaignEventId);
		campaignRuleDAO.saveOne(campaignRuleUse);
		CampaignRule campaignRuleExchange = createCampaignRuleExchange(campaignEventId);
		campaignRuleDAO.saveOne(campaignRuleExchange);
		CampaignRule campaignRuleWxInitiate = createCampaignRuleWx(campaignEventId);
		campaignRuleWxInitiate.setType(CfConstants.WX_INITIATE);
		campaignRuleDAO.saveOne(campaignRuleWxInitiate);
		CampaignRule campaignRuleWxParticipate = createCampaignRuleWx(campaignEventId);
		campaignRuleWxParticipate.setType(CfConstants.WX_PARTICIPATE);
		campaignRuleDAO.saveOne(campaignRuleWxParticipate);
		// 活动票券池数据
		CampaignCoupon compaignCoupon = createCampaignCoupon(campaignId, campaignEventId);
		campaignCouponDAO.saveOne(compaignCoupon);
		CampaignCoupon savedCampaignCoupon = campaignCouponDAO.findByParentId(campaignId).get(0);
		// 活动票券数据
		List<CampaignCouponItem> campaignCouponItemList = createCampaignCouponItem(savedCampaignCoupon);
		for (CampaignCouponItem campaignCouponItem : campaignCouponItemList)
			campaignCouponItemDAO.saveOne(campaignCouponItem);
	}

	private static Campaign createCampaign() {
		Campaign ret = new Campaign();
		Date now = new Date();
		ret.setCode("CampaignCode");
		ret.setName("活动");
		ret.setStatus(CampaignStatus.IN_PROCESS.toString());
		ret.setStatusDate(now);
		ret.setStartDate(now);
		ret.setEndDate(DateUtils.addDays(now, 7));
		ret.setDesc("活动");
		return ret;
	}

	private static CampaignEvent createCampaignEvent(Long campaignId) {
		CampaignEvent ret = new CampaignEvent();
		ret.setCampaignId(campaignId);
		ret.setCode("CampaignEventCode");
		ret.setDesc("活动事件");
		return ret;
	}

	private static CampaignRule createCampaignRuleParticipated(Long campaignEventId) {
		String PARTICIPATED_Condition = "if (campaignExecution.countCampaignResponse(campaignRequest) > 0) throw new Exception(\"你已经参加过活动了！\"); return true;";
		String PARTICIPATED_Body = "campaignResponse.setStatus(\"PARTICIPATED\");";

		CampaignRule ret = new CampaignRule();
		Date now = new Date();
		ret.setCampaignEventId(campaignEventId);
		ret.setType(CfConstants.PARTICIPATED);
		ret.setGroovyCondition(PARTICIPATED_Condition);
		ret.setGroovyBody(PARTICIPATED_Body);
		ret.setStartDate(now);
		ret.setEndDate(DateUtils.addDays(now, 7));
		return ret;
	}

	private static CampaignRule createCampaignRuleUse(Long campaignEventId) {
		String EXCHANGE_Condition = "CampaignCouponItem campaignCouponItem = campaignCouponItemDAO.findOneBySerialNumber(campaignEventDAO.findId(campaignEventCode), serialNumber); if (!StringUtils.equalsIgnoreCase(campaignCouponItem.getStatus(), \"DISTRIBUTED\")) throw new Exception(\"serialNumber = \" + serialNumber + \" is NOT abailable\"); if (!StringUtils.equals (campaignCouponItem.getMobile(), mobile)) throw new Exception(\"serialNumber = \" + serialNumber + \"所有者信息不匹配！\"); return true;";
		String EXCHANGE_Body = "CampaignCouponItem campaignCouponItem = campaignCouponItemDAO.findOneBySerialNumber(campaignEventDAO.findId(campaignEventCode), serialNumber); campaignExecution.bindCampaignCoupon(campaignCouponItem, campaignRequest); campaignResponse.setStatus(\"USED\");";

		CampaignRule ret = new CampaignRule();
		Date now = new Date();
		ret.setCampaignEventId(campaignEventId);
		ret.setType(CfConstants.USED);
		ret.setGroovyCondition(EXCHANGE_Condition);
		ret.setGroovyBody(EXCHANGE_Body);
		ret.setStartDate(now);
		ret.setEndDate(DateUtils.addDays(now, 7));
		return ret;
	}

	private static CampaignRule createCampaignRuleExchange(Long campaignEventId) {
		String EXCHANGE_Condition = "CampaignCouponItem campaignCouponItem = campaignCouponItemDAO.findOneBySerialNumber(campaignEventDAO.findId(campaignEventCode), serialNumber); if (!StringUtils.equalsIgnoreCase(campaignCouponItem.getStatus(), \"AVAILABLE\")) throw new Exception(\"serialNumber = \" + serialNumber + \" is NOT abailable\"); return true;";
		String EXCHANGE_Body = "CampaignCouponItem campaignCouponItem = campaignCouponItemDAO.findOneBySerialNumber(campaignEventDAO.findId(campaignEventCode), serialNumber); campaignExecution.bindCampaignCoupon(campaignCouponItem, campaignRequest); campaignExecution.distributeCampaignCoupon(campaignRequest); campaignResponse.setStatus(\"EXCHANGE\");";

		CampaignRule ret = new CampaignRule();
		Date now = new Date();
		ret.setCampaignEventId(campaignEventId);
		ret.setType(CfConstants.EXCHANGE);
		ret.setGroovyCondition(EXCHANGE_Condition);
		ret.setGroovyBody(EXCHANGE_Body);
		ret.setStartDate(now);
		ret.setEndDate(DateUtils.addDays(now, 7));
		return ret;
	}

	private static CampaignRule createCampaignRuleWx(Long campaignEventId) {
		String WX_Condition = "if (campaignExecution.hasParticipatedWxCampaign(campaignRequest)) throw new Exception(\"你已经参加过微信组团活动了！\"); return true;";
		String WX_Body = "if (StringUtils.equalsIgnoreCase(campaignRequest.getRuleType(), CfConstants.WX_INITIATE)) { campaignResponse.setStatus(CfConstants.WX_INITIATE); } else if  (StringUtils.equalsIgnoreCase(campaignRequest.getRuleType(), CfConstants.WX_PARTICIPATE)) { campaignResponse.setReserved(campaignExecution.resolveCampaignRequest(campaignRequest, CfConstants.RESOLVE_TYPE_WX).get(CfConstants.RESOLVE_targetOpenId)); campaignResponse.setStatus(CfConstants.WX_PARTICIPATE); }";

		CampaignRule ret = new CampaignRule();
		Date now = new Date();
		ret.setCampaignEventId(campaignEventId);
		ret.setGroovyCondition(WX_Condition);
		ret.setGroovyBody(WX_Body);
		ret.setStartDate(now);
		ret.setEndDate(DateUtils.addDays(now, 7));
		return ret;
	}

	private static CampaignCoupon createCampaignCoupon(Long campaignId, Long campaignEventId) {
		CampaignCoupon ret = new CampaignCoupon();
		Date now = new Date();
		ret.setCampaignId(campaignId);
		ret.setCampaignEventId(campaignEventId);
		ret.setCode("COUPON");
		ret.setName("活动票券");
		ret.setType(CampaignCouponType.COUPON.toString());
		ret.setDesc("活动票券");
		ret.setTotalAmount(10L);
		ret.setAvailableAmount(10L);
		ret.setStartDate(now);
		ret.setEndDate(DateUtils.addDays(now, CfConstants.CampainCouponExpireDays));
		return ret;
	}

	private static List<CampaignCouponItem> createCampaignCouponItem(CampaignCoupon campaignCoupon) {
		List<CampaignCouponItem> ret = new ArrayList<CampaignCouponItem>();
		Date now = new Date();
		for (Long i = 0L; i != campaignCoupon.getTotalAmount(); i++) {
			CampaignCouponItem tmp = new CampaignCouponItem(campaignCoupon);
			tmp.setSerialNumber(UUID.randomUUID().toString());
			tmp.setStatusDate(now);
			ret.add(tmp);
		}
		return ret;
	}
}
