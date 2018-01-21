package jie.cf.core.dao;

import jie.cf.core.dto.Campaign;

/**
 * Data access layer for the Campaign
 * 
 * @author Jie
 *
 */
public interface ICampaignDAO {
	/**
	 * 持久化：CREATE or UPDATE
	 * 
	 * @param campaign
	 */
	void saveOne(Campaign campaign);

	/**
	 * 
	 * @param id
	 *            活动ID
	 * @return
	 */
	Campaign findOne(Long id);

	/**
	 * 
	 * @param code
	 *            活动CODE
	 * @return
	 */
	Campaign findOne(String code);

	/**
	 * 通过code查找ID。唯一性
	 * 
	 * @param code
	 * @return
	 */
	Long findId(String code);
}
