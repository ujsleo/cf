package jie.cf.demo.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jie.cf.core.dao.ICampaignCouponDAO;
import jie.cf.core.dto.CampaignCoupon;
import jie.cf.core.utils.stereotype.CfDemo;

@CfDemo
public class DemoCampaignCouponDAO implements ICampaignCouponDAO {
	private Map<String, Object> repo = new HashMap<String, Object>();
	private static Long id = 1L;

	@Override
	public void saveOne(CampaignCoupon campaignCoupon) {
		if (campaignCoupon.getId() == null)
			campaignCoupon.setId(id++);
		String key = campaignCoupon.getId() + ":" + campaignCoupon.getCode();
		repo.put(key, campaignCoupon);
	}

	@Override
	public CampaignCoupon findOne(Long id) {
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[0].equals(id.toString()))
				return (CampaignCoupon) repo.get(key);
		}
		return null;
	}

	@Override
	public CampaignCoupon findOne(String code) {
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[1].equals(code))
				return (CampaignCoupon) repo.get(key);
		}
		return null;
	}

	@Override
	public List<CampaignCoupon> findByParentId(Long campaignId) {
		List<CampaignCoupon> ret = new ArrayList<CampaignCoupon>();
		for (Map.Entry<String, Object> entry : repo.entrySet()) {
			CampaignCoupon tmp = (CampaignCoupon) entry.getValue();
			if (tmp.getCampaignId().equals(campaignId))
				ret.add(tmp);
		}
		return ret;
	}
}
