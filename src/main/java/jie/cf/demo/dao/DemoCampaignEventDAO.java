package jie.cf.demo.dao;

import java.util.HashMap;
import java.util.Map;

import jie.cf.core.dao.ICampaignEventDAO;
import jie.cf.core.dto.CampaignEvent;
import jie.cf.core.utils.stereotype.CfDemo;

@CfDemo
public class DemoCampaignEventDAO implements ICampaignEventDAO {
	private Map<String, Object> repo = new HashMap<String, Object>();
	private static Long id = 1L;

	@Override
	public void saveOne(CampaignEvent campaignEvent) {
		if (campaignEvent.getId() == null)
			campaignEvent.setId(id++);
		String key = campaignEvent.getId() + ":" + campaignEvent.getCode();
		repo.put(key, campaignEvent);
	}

	@Override
	public CampaignEvent findOne(Long id) {
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[0].equals(id.toString()))
				return (CampaignEvent) repo.get(key);
		}
		return null;
	}

	@Override
	public CampaignEvent findOne(String code) {
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[1].equals(code))
				return (CampaignEvent) repo.get(key);
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
