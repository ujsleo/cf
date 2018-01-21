package jie.cf.core.dao;

import java.util.List;

import jie.cf.core.dto.CampaignResponse;

/**
 * Data access layer for the CampaignResponse
 * 
 * @author Jie
 *
 */
public interface ICampaignResponseDAO {
	/**
	 * 持久化：CREATE or UPDATE
	 * 
	 * @param campaignResponse
	 */
	void saveOne(CampaignResponse campaignResponse);

	/**
	 * 
	 * @param id
	 *            活动响应ID
	 * @return
	 */
	CampaignResponse findOne(Long id);

	/**
	 * 通过任一指定参数查找相关的活动响应列表
	 * 
	 * @param campaignEventId
	 *            活动事件ID. required
	 * @param clientId
	 *            客户ID. optional
	 * @param mobile
	 *            手机号. optional
	 * @return
	 */
	List<CampaignResponse> findByCampaignRequest(Long campaignEventId, Long clientId, String mobile);

	/**
	 * 通过微信OpenId查找相关的活动响应列表
	 * 
	 * @param campaignEventId
	 *            活动事件ID. required
	 * @param openId
	 *            参与人微信OpenId. required
	 * @param targetOpenId
	 *            目标发起人微信OpenId. optional
	 * @return
	 */
	List<CampaignResponse> findByWxOpenId(Long campaignEventId, String openId, String targetOpenId);
}
