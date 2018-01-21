package jie.cf.demo.dao;

import java.util.HashMap;
import java.util.Map;

import jie.cf.core.dao.ICampaignDAO;
import jie.cf.core.dto.Campaign;
import jie.cf.core.utils.stereotype.CfDemo;

@CfDemo
public class DemoCampaignDAO implements ICampaignDAO {
	private Map<String, Object> repo = new HashMap<String, Object>();
	private static Long id = 1L;

	@Override
	public void saveOne(Campaign campaign) {
		if (campaign.getId() == null)
			campaign.setId(id++);
		String key = campaign.getId() + ":" + campaign.getCode();
		repo.put(key, campaign);
	}

	@Override
	public Campaign findOne(Long id) {
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[0].equals(id.toString()))
				return (Campaign) repo.get(key);
		}
		return null;
	}

	@Override
	public Campaign findOne(String code) {
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[1].equals(code))
				return (Campaign) repo.get(key);
		}
		return null;
	}

	@Override
	public Long findId(String code) {
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[1].equals(code))
				return Long.parseLong(items[0]);
		}
		return null;
	}
}
