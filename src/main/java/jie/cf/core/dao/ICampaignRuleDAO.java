package jie.cf.core.dao;

import java.util.List;

import jie.cf.core.dto.CampaignRule;

/**
 * Data access layer for the CampaignRule
 * 
 * @author Jie
 *
 */
public interface ICampaignRuleDAO {
	/**
	 * 持久化：CREATE or UPDATE
	 * 
	 * @param campaignRule
	 */
	void saveOne(CampaignRule campaignRule);

	/**
	 * 
	 * @param id
	 *            活动规则ID
	 * @return
	 */
	CampaignRule findOne(Long id);

	/**
	 * 通过活动事件ID和规则类型查找相关的活动规则列表
	 * 
	 * @param campaignEventId
	 * @param type
	 * @return
	 */
	List<CampaignRule> findByParentId(Long campaignEventId, String type);
}
