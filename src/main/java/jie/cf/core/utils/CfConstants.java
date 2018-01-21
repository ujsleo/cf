package jie.cf.core.utils;

public class CfConstants {
	/** 票券有效期（天） */
	public static final int CampainCouponExpireDays = 30;

	private static final String CONTEXT_IMPORTS = "import org.springframework.context.ApplicationContext;";
	private static final String DAO_IMPORTS = "import jie.cf.core.dao.ICampaignDAO;"
			+ "import jie.cf.core.dao.ICampaignEventDAO;" + "import jie.cf.core.dao.ICampaignRuleDAO;"
			+ "jie.cf.core.dao.ICampaignCouponDAO;" + "jie.cf.core.dao.ICampaignCouponItemDAO;";
	private static final String SERVICE_IMPORTS = "import jie.cf.core.service.CampaignExecution;"
			+ "import jie.cf.core.utils.CfUtils;";
	private static final String DTO_IMPORTS = "import jie.cf.core.dto.CampaignRequest;"
			+ "import jie.cf.core.dto.CampaignResponse;" + "import jie.cf.core.dto.CampaignCouponItem;";
	private static final String CONSTANTS_IMPORTS = "import jie.cf.core.utils.CfConstants;";
	private static final String UTILS_IMPORTS = "import org.apache.commons.lang3.StringUtils;";
	/** Groovy脚本import包 */
	public static final String GROOVY_IMPORTS = CONTEXT_IMPORTS + DAO_IMPORTS + SERVICE_IMPORTS + DTO_IMPORTS
			+ CONSTANTS_IMPORTS + UTILS_IMPORTS;

	// === 活动规则类型、活动响应状态 ===
	/** 活动规则类型、活动响应状态：参与 */
	public static final String PARTICIPATED = "PARTICIPATED";
	/** 活动规则类型、活动响应状态：兑换票券 */
	public static final String EXCHANGE = "EXCHANGE";
	/** 活动规则类型、活动响应状态：派发票券 */
	public static final String DISTRIBUTED = "DISTRIBUTED";
	/** 活动规则类型、活动响应状态：使用票券 */
	public static final String USED = "USED";
	/** 活动规则类型、活动响应状态：微信朋友帮砍价活动 - 发起人 */
	public static final String WX_INITIATE = "WX_INITIATE";
	/** 活动规则类型、活动响应状态：微信朋友帮砍价活动 - 参与人 */
	public static final String WX_PARTICIPATE = "WX_PARTICIPATE";

	// === 解析参数类型 ===
	/** 解析参数类型：派发票券 */
	public static final int RESOLVE_TYPE_DISTRIBUTE = 2;
	/** 解析参数类型：微信“好友帮砍价” */
	public static final int RESOLVE_TYPE_WX = 16;

	// === 解析参数 ===
	/** 解析参数：票券池CODE */
	public static final String RESOLVE_campaignCouponCode = "campaignCouponCode";
	/** 解析参数：票券状态 */
	public static final String RESOLVE_campaignCouponItemStatus = "campaignCouponItemStatus";
	/** 解析参数：票券数量 */
	public static final String RESOLVE_campaignCouponItemAmount = "campaignCouponItemAmount";
	/** 解析参数：目标微信OpenId */
	public static final String RESOLVE_targetOpenId = "targetOpenId";
}
