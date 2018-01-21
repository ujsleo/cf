package jie.cf.core.dao;

import jie.cf.core.dto.CampaignEvent;

/**
 * Data access layer for the CampaignEvent
 * 
 * @author Jie
 *
 */
public interface ICampaignEventDAO {
	/**
	 * 持久化：CREATE or UPDATE
	 * 
	 * @param campaignEvent
	 */
	void saveOne(CampaignEvent campaignEvent);

	/**
	 * 
	 * @param id
	 *            活动事件ID
	 * @return
	 */
	CampaignEvent findOne(Long id);

	/**
	 * 
	 * @param code
	 *            活动事件CODE
	 * @return
	 */
	CampaignEvent findOne(String code);

	/**
	 * 通过code查找ID。唯一性
	 * 
	 * @param code
	 * @return
	 */
	Long findId(String code);
}
