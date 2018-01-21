package jie.cf.core.dto;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 票券
 * 
 * @author Jie
 *
 */
public class CampaignCouponItem {
	private Long id;
	private Long campaignId; // 活动ID
	private Long campaignEventId; // 活动事件ID
	private Long campaignCouponId; // 票券池ID
	private String type; // 票券类型。同CampaignCoupon.type
	private String serialNumber; // 票券序列号
	private String status; // 票券状态
	private Date statusDate; // 票券状态的更新时间
	private String desc; // 描述
	private Long clientId; // 客户ID
	private String mobile; // 手机号
	private String openId; // 微信openId
	private Date bindingDate; // 票券绑定到用户的时间
	private Date startDate; // 票券开始时间
	private Date endDate; // 票券结束时间

	public CampaignCouponItem() {
	}

	public CampaignCouponItem(CampaignCoupon campaignCoupon) {
		campaignId = campaignCoupon.getCampaignId();
		campaignEventId = campaignCoupon.getCampaignEventId();
		campaignCouponId = campaignCoupon.getId();
		type = campaignCoupon.getType();
		desc = campaignCoupon.getDesc();
		startDate = campaignCoupon.getStartDate();
		endDate = campaignCoupon.getEndDate();
		status = CampaignCouponItemStatus.AVAILABLE.toString();
	}

	/** 票券状态 */
	public enum CampaignCouponItemStatus {
		AVAILABLE, // 可用
		PREPARED, // 待派发
		DISTRIBUTED, // 已派发
		USED, // 已使用
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

	public Long getCampaignCouponId() {
		return campaignCouponId;
	}

	public void setCampaignCouponId(Long campaignCouponId) {
		this.campaignCouponId = campaignCouponId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	public Date getBindingDate() {
		return bindingDate;
	}

	public void setBindingDate(Date bindingDate) {
		this.bindingDate = bindingDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
