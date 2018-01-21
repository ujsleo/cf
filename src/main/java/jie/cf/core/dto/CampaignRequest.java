package jie.cf.core.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 活动请求
 * 
 * @author Jie
 *
 */
public class CampaignRequest {
	private String campaignEventCode; // 活动事件CODE
	private Long clientId; // 客户ID
	private String mobile; // 手机号
	private String openId; // 微信openId
	private String ruleType; // 规则类型（选填）
	private String serialNumber; // 兑换票券
	private String reserved; // 预留。建议JSON

	@Override
	public String toString() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getCampaignEventCode() {
		return campaignEventCode;
	}

	public void setCampaignEventCode(String campaignEventCode) {
		this.campaignEventCode = campaignEventCode;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getReserved() {
		return reserved;
	}

	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
}
