package jie.cf.core.dao;

import java.util.List;

import jie.cf.core.dto.CampaignCouponItem;
import jie.cf.core.utils.stereotype.CfDemo;

/**
 * Data access layer for the CampaignCouponItem
 * 
 * @author Jie
 *
 */
public interface ICampaignCouponItemDAO {
	/**
	 * 持久化：CREATE or UPDATE
	 * 
	 * @param campaignCouponItem
	 */
	void saveOne(CampaignCouponItem campaignCouponItem);

	/**
	 * 
	 * @param id
	 *            票券ID
	 * @return
	 */
	CampaignCouponItem findOne(Long id);

	/**
	 * 通过活动事件ID和序列号查找一张活动票券
	 * 
	 * @param campaignEventId
	 * @param serialNumber
	 * @return
	 */
	CampaignCouponItem findOneBySerialNumber(Long campaignEventId, String serialNumber);

	/**
	 * 通过任一指定参数查找相关的票券列表
	 * 
	 * @param clientId
	 *            客户ID. optional
	 * @param mobile
	 *            手机号. optional
	 * @return
	 */
	List<CampaignCouponItem> findByUser(Long clientId, String mobile);

	/**
	 * 通过活动事件ID查找一张AVAILABLE活动票券
	 * 
	 * @param campaignEventId
	 * @return
	 */
	@CfDemo
	@Deprecated
	CampaignCouponItem findOneAvailable(Long campaignEventId);
}
