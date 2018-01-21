package jie.cf.demo.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jie.cf.core.dao.ICampaignRuleDAO;
import jie.cf.core.dto.CampaignRule;
import jie.cf.core.utils.stereotype.CfDemo;

@CfDemo
public class DemoCampaignRuleDAO implements ICampaignRuleDAO {
	private Map<String, Object> repo = new HashMap<String, Object>();
	private static Long id = 1L;

	@Override
	public void saveOne(CampaignRule campaignRule) {
		if (campaignRule.getId() == null)
			campaignRule.setId(id++);
		String key = campaignRule.getId() + ":" + campaignRule.getCampaignEventId();
		repo.put(key, campaignRule);
	}

	@Override
	public CampaignRule findOne(Long id) {
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[0].equals(id.toString()))
				return (CampaignRule) repo.get(key);
		}
		return null;
	}

	@Override
	public List<CampaignRule> findByParentId(Long campaignEventId, String type) {
		List<CampaignRule> ret = new ArrayList<CampaignRule>();
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[1].equals(campaignEventId.toString())) {
				CampaignRule tmp = (CampaignRule) repo.get(key);
				if (tmp.getType().equals(type))
					ret.add(tmp);
			}
		}
		return ret;
	}
}
