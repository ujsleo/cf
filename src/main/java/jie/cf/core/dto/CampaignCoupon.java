package jie.cf.core.dto;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 票券池
 * 
 * @author Jie
 *
 */
public class CampaignCoupon {
	private Long id;
	private Long campaignId; // 活动ID
	private Long campaignEventId; // 活动事件ID
	private String code; // 票券池CODE。UNIQUE
	private String name; // 票券池名
	private String type; // 票券类型
	private String desc; // 描述
	private Long totalAmount; // 总票券数
	private Long availableAmount; // 可用票券数
	private Date startDate; // 票券默认开始时间
	private Date endDate; // 票券默认结束时间

	/** 票券类型 */
	public enum CampaignCouponType {
		COUPON, // 内部票券
		TICKET, // 外部票券（如电影票）兑换码
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(Long availableAmount) {
		this.availableAmount = availableAmount;
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
