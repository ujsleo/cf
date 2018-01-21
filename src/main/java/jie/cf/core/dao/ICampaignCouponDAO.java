package jie.cf.core.dao;

import java.util.List;

import jie.cf.core.dto.CampaignCoupon;

/**
 * Data access layer for the CampaignCoupon
 * 
 * @author Jie
 *
 */
public interface ICampaignCouponDAO {
	/**
	 * 持久化：CREATE or UPDATE
	 * 
	 * @param campaignCoupon
	 */
	void saveOne(CampaignCoupon campaignCoupon);

	/**
	 * 
	 * @param id
	 *            票券池ID
	 * @return
	 */
	CampaignCoupon findOne(Long id);

	/**
	 * 
	 * @param code
	 *            票券池CODE
	 * @return
	 */
	CampaignCoupon findOne(String code);

	/**
	 * 通过活动ID查找相关的活动票券池列表
	 * 
	 * @param campaignId
	 * @return
	 */
	List<CampaignCoupon> findByParentId(Long campaignId);
}
