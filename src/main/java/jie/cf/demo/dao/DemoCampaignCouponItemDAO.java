package jie.cf.demo.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import jie.cf.core.dao.ICampaignCouponItemDAO;
import jie.cf.core.dto.CampaignCouponItem;
import jie.cf.core.dto.CampaignCouponItem.CampaignCouponItemStatus;
import jie.cf.core.utils.stereotype.CfDemo;

@CfDemo
public class DemoCampaignCouponItemDAO implements ICampaignCouponItemDAO {
	private Map<String, Object> repo = new HashMap<String, Object>();
	private static Long id = 1L;

	@Override
	public void saveOne(CampaignCouponItem campaignCouponItem) {
		if (campaignCouponItem.getId() == null)
			campaignCouponItem.setId(id++);
		String key = campaignCouponItem.getId() + ":" + campaignCouponItem.getCampaignEventId();
		repo.put(key, campaignCouponItem);
	}

	@Override
	public CampaignCouponItem findOne(Long id) {
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[0].equals(id.toString()))
				return (CampaignCouponItem) repo.get(key);
		}
		return null;
	}

	@Override
	public CampaignCouponItem findOneBySerialNumber(Long campaignEventId, String serialNumber) {
		CampaignCouponItem ret = null;
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[1].equals(campaignEventId.toString())) {
				CampaignCouponItem tmp = (CampaignCouponItem) repo.get(key);
				if (StringUtils.equals(serialNumber, tmp.getSerialNumber()))
					return tmp;
			}
		}
		return ret;
	}

	@Override
	public List<CampaignCouponItem> findByUser(Long clientId, String mobile) {
		List<CampaignCouponItem> ret = new ArrayList<CampaignCouponItem>();
		for (String key : repo.keySet()) {
			CampaignCouponItem tmp = (CampaignCouponItem) repo.get(key);
			if (tmp.getClientId() == clientId || StringUtils.equals(mobile, tmp.getMobile()))
				ret.add(tmp);
		}
		return ret;
	}

	@Override
	public CampaignCouponItem findOneAvailable(Long campaignEventId) {
		CampaignCouponItem ret = null;
		for (String key : repo.keySet()) {
			String[] items = key.split(":");
			if (items[1].equals(campaignEventId.toString())) {
				CampaignCouponItem tmp = (CampaignCouponItem) repo.get(key);
				if (StringUtils.equals(CampaignCouponItemStatus.AVAILABLE.toString(), tmp.getStatus()))
					return tmp;
			}
		}
		return ret;
	}
}
