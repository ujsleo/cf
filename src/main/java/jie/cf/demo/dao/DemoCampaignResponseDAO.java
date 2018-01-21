package jie.cf.demo.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import jie.cf.core.dao.ICampaignResponseDAO;
import jie.cf.core.dto.CampaignResponse;
import jie.cf.core.utils.stereotype.CfDemo;

@CfDemo
public class DemoCampaignResponseDAO implements ICampaignResponseDAO {
	private Map<String, Object> repo = new HashMap<String, Object>();
	private static Long id = 1L;

	@Override
	public void saveOne(CampaignResponse campaignResponse) {
		if (campaignResponse.getId() == null)
			campaignResponse.setId(id++);
		String key = campaignResponse.getId() + ":" + campaignResponse.getCampaignEventId();
		repo.put(key, campaignResponse);
	}

	@Override
	public CampaignResponse findOne(Long id) {
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[0].equals(id.toString()))
				return (CampaignResponse) repo.get(key);
		}
		return null;
	}

	@Override
	public List<CampaignResponse> findByCampaignRequest(Long campaignEventId, Long clientId, String mobile) {
		List<CampaignResponse> ret = new ArrayList<CampaignResponse>();
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[1].equals(campaignEventId.toString())) {
				CampaignResponse tmp = (CampaignResponse) repo.get(key);
				if (tmp.getClientId() == clientId || tmp.getMobile().equals(mobile))
					ret.add(tmp);
			}
		}
		return ret;
	}

	@Override
	public List<CampaignResponse> findByWxOpenId(Long campaignEventId, String openId, String targetOpenId) {
		List<CampaignResponse> ret = new ArrayList<CampaignResponse>();
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[1].equals(campaignEventId.toString())) {
				CampaignResponse tmp = (CampaignResponse) repo.get(key);
				if (tmp.getOpenId().equals(openId) || StringUtils.equals(targetOpenId, tmp.getReserved()))
					ret.add(tmp);
			}
		}
		return ret;
	}
}
