package jie.cf.core.dto;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jie.cf.core.utils.CfConstants;

/**
 * 活动响应
 * 
 * @author Jie
 *
 */
public class CampaignResponse {
	private Long id;
	private Long campaignId; // 活动ID
	private Long campaignEventId; // 活动事件ID
	private Long campaignRuleId; // 活动规则ID
	private Long clientId; // 客户ID
	private String mobile; // 手机号
	private String openId; // 微信openId
	private String status; // 活动响应状态。同CampaignRule.type
	private Date statusDate; // 活动响应状态的更新时间
	private String reserved; // 预留。建议JSON

	public CampaignResponse() {
	}

	public CampaignResponse(CampaignRequest campaignRequest) {
		clientId = campaignRequest.getClientId();
		mobile = campaignRequest.getMobile();
		openId = campaignRequest.getOpenId();
		status = CfConstants.PARTICIPATED;
	}

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public Long getCampaignEventId() {
		return campaignEventId;
	}

	public void setCampaignEventId(Long campaignEventId) {
		this.campaignEventId = campaignEventId;
	}

	public Long getCampaignRuleId() {
		return campaignRuleId;
	}

	public void setCampaignRuleId(Long campaignRuleId) {
		this.campaignRuleId = campaignRuleId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public String getReserved() {
		return reserved;
	}

	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
}
