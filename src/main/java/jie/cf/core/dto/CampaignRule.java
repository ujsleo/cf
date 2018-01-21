package jie.cf.core.dto;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 活动规则
 * 
 * @author Jie
 *
 */
public class CampaignRule {
	private Long id;
	private Long campaignEventId; // 活动事件ID
	private String type; // 活动规则类型
	private String groovyCondition; // 判断条件
	private String groovyBody; // 执行代码
	private Date startDate; // 活动规则开始时间
	private Date endDate; // 活动规则结束时间

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

	public Long getCampaignEventId() {
		return campaignEventId;
	}

	public void setCampaignEventId(Long campaignEventId) {
		this.campaignEventId = campaignEventId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGroovyCondition() {
		return groovyCondition;
	}

	public void setGroovyCondition(String groovyCondition) {
		this.groovyCondition = groovyCondition;
	}

	public String getGroovyBody() {
		return groovyBody;
	}

	public void setGroovyBody(String groovyBody) {
		this.groovyBody = groovyBody;
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
