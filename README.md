# CF - Campaign Framework, 营销活动框架
  (Jie © 2017)<br>
## 1 分层架构设计
CF（营销活动框架，Campaign Framework）基于模板方法和策略模式设计。将“变化的部分”通过Groovy脚本来实现并与业务代码解耦合，脚本通过Groovy Bindings可使用CF框架基础服务bean、获取业务数据(**CampaignRequest**)、修改活动响应数据(**CampaignResponse**)等。<br />
![CF分层架构](/dev-book/uml/Architecture.png)<br />
### 1.1 时序图
![时序图](/dev-book/uml/SequenceDiagram.png)<br />
### 1.2 Quick start
Spring Boot App - Run<br />
http://localhost:8080/ 执行Groovy脚本
#### 1.2.1 参加活动
campaignApi.campaignEvent(campaignRequest);
> campaignRequest参数中campaignEventCode必填；clientId, mobile, openId不能都为null

GroovyCondition
```
// 先判断用户是否满足活动条件
if (!活动条件)
  throw new Exception("不满足活动条件！");

// 统计用户参加活动的次数
if (campaignExecution.countCampaignResponse(campaignRequest) > 0)
  throw new Exception("你已经参加过活动了！");

return true;
```
GroovyBody
```
// 修改活动响应数据
campaignResponse.setStatus("PARTICIPATED");
```
#### 1.2.2 使用票券
campaignApi.use(campaignRequest);
> campaignRequest参数中campaignEventCode, serialNumber必填；clientId, mobile, openId不能都为null

GroovyCondition
```
// 获取对应的票券对象
CampaignCouponItem campaignCouponItem = campaignCouponItemDAO.findOneBySerialNumber(campaignEventDAO.findId(campaignEventCode), serialNumber);
// 校验票券
if (!StringUtils.equalsIgnoreCase(campaignCouponItem.getStatus(), "DISTRIBUTED"))
  throw new Exception("serialNumber = " + serialNumber + " is NOT abailable");
if (!StringUtils.equals (campaignCouponItem.getMobile(), mobile))
  throw new Exception("serialNumber = " + serialNumber + "所有者信息不匹配！");
return true;
```
GroovyBody
```
// 获取对应的票券对象
CampaignCouponItem campaignCouponItem = campaignCouponItemDAO.findOneBySerialNumber(campaignEventDAO.findId(campaignEventCode), serialNumber);
// 使用票券，修改活动响应数据
campaignExecution.bindCampaignCoupon(campaignCouponItem, campaignRequest);
campaignResponse.setStatus("USED");
```
#### 1.2.3 兑换票券
campaignApi.exchange(campaignRequest);
> campaignRequest参数中campaignEventCode, serialNumber必填；reserved字段转JSON = {"campaignCouponCode":"必填","campaignCouponItemStatus":"选填","campaignCouponItemAmount":"选填"}；clientId, mobile, openId不能都为null

GroovyCondition
```
// 获取对应的票券对象
CampaignCouponItem campaignCouponItem = campaignCouponItemDAO.findOneBySerialNumber(campaignEventDAO.findId(campaignEventCode), serialNumber);
// 校验票券
if (!StringUtils.equalsIgnoreCase(campaignCouponItem.getStatus(), "AVAILABLE"))
  throw new Exception("serialNumber = " + serialNumber + " is NOT abailable");
return true;
```
GroovyBody
```
// 获取对应的票券对象
CampaignCouponItem campaignCouponItem = campaignCouponItemDAO.findOneBySerialNumber(campaignEventDAO.findId(campaignEventCode), serialNumber);
// 使用兑换码
campaignExecution.bindCampaignCoupon(campaignCouponItem, campaignRequest);
// 派发指定数目的票券，修改活动响应数据
campaignExecution.distributeCampaignCoupon(campaignRequest);
campaignResponse.setStatus("EXCHANGE");

```
#### 1.2.4 微信“好友帮砍价”活动
campaignApi.wechatCampaignEvent(campaignRequest);
> campaignRequest参数中campaignEventCode, openId, ruleType必填；参与人reserved字段转JSON = {"targetOpenId":"发起人OpenId"}

GroovyCondition
```
// 先判断用户是否满足活动条件
if (campaignExecution.hasParticipatedWxCampaign(campaignRequest))
  throw new Exception("你已经参加过微信组团活动了！");
return true;
```
GroovyBody
```
// 修改活动响应数据
if (StringUtils.equalsIgnoreCase(campaignRequest.getRuleType(), CfConstants.WX_INITIATE)) {
  // 发起人
  campaignResponse.setStatus(CfConstants.WX_INITIATE);
} else if  (StringUtils.equalsIgnoreCase(campaignRequest.getRuleType(), CfConstants.WX_PARTICIPATE)) {
  // 参与人
  campaignResponse.setReserved(campaignExecution.resolveCampaignRequest(campaignRequest, CfConstants.RESOLVE_TYPE_WX).get(CfConstants.RESOLVE_targetOpenId));
  campaignResponse.setStatus(CfConstants.WX_PARTICIPATE);
}
```
## 2 CfCampaignSvs服务
CfCampaignSvs服务负责校验活动请求、读取查询并执行活动规则等。若执行成功，则记录一条活动响应；否则抛异常。
### 2.1 Campaign类：活动
域 | 描述 | 备注
---|---|---
code | 活动CODE | 活动CODE
name | 活动名 | 
status | 活动状态 | **NOT_START** 未开始 **IN_PROCESS** 进行中 **END** 已结束
statusDate | 活动状态的更新时间 | 
startDate | 活动开始时间 | 活动的有效期
endDate | 活动结束时间 | 
desc | 描述 | 

### 2.2 CampaignEvent类：活动事件
域 | 描述 | 备注
---|---|---
campaignId | 活动ID | 活动CODE
code | 活动事件CODE | UNIQUE
desc | 描述 | 

### 2.3 CampaignRule类：活动规则
域 | 描述 | 备注
---|---|---
campaignId | campaignId | 
campaignEventId | 活动事件ID | 
name | 活动规则名 | 
type | 活动规则类型 | **PARTICIPATED** 参与 **EXCHANGE** 兑换票券 **DISTRIBUTED** 派发票券 **USED** 使用票券
groovyCondition | 判断条件 | Groovy脚本
groovyBody | 执行代码 | Groovy脚本
startDate | 活动规则开始时间 | 
endDate | 活动规则结束时间 | 

### 2.4 CampaignResponse类：活动响应
域 | 描述 | 备注
---|---|---
campaignId | 活动ID | 
campaignEventId | 活动事件ID | 
campaignRuleId | 活动规则ID | 
clientId | 客户ID | 
mobile | 手机号 | 
openId | 微信openId | 
status | 活动响应状态 | 同CampaignRule.type
statusDate | 活动响应状态的更新时间 | 	
reserved | 预留 | 建议JSON

### 2.5 CampaignCoupon类：票券池
域 | 描述 | 备注
---|---|---
 |  | 
campaignId | 活动ID | 
campaignEventId | 活动事件ID | 	
code | 票券池CODE | UNIQUE
name | 票券池名 | 
type | 票券类型 | **COUPON** 内部票券 **TICKET** 外部票券（如电影票）兑换码
desc | 描述 | 
totalAmount | 总票券数 | 
availableAmount | 可用票券数 | 
startDate | 票券默认开始时间 | 
endDate | 票券默认结束时间 | 

### 2.6 CampaignCouponItem类：票券
域 | 描述 | 备注
---|---|---
 |  | 
campaignId | 活动ID | 
campaignEventId | 活动事件ID | 
campaignCouponId | 票券池ID | 
type | 票券类型 | 同CampaignCoupon.type
serialNumber | 票券序列号 | 
status | 票券状态 | **AVAILABLE** 可用 **PREPARED** 待派发 **DISTRIBUTED** 已派发 **USED** 已使用
statusDate | 票券状态的更新时间 | 
desc | 描述 | 
clientId | 客户ID | 
mobile | 手机号 | 
openId | 微信openId | 
bindingDate | 票券绑定到用户的时间 | 
startDate | 票券开始时间 | 
endDate | 票券结束时间 | 

## 3 附录
### 3.1 Groovy Bindings
变量名 | 变量类型 | 备注
---|---|---
context | ApplicationContext | 	Spring应用上下文 bean
campaignDAO | ICampaignDAO | 活动DAO bean
campaignEventDAO | ICampaignEventDAO | 	活动事件DAO bean
campaignRuleDAO | ICampaignRuleDAO | 活动规则DAO bean
campaignCouponDAO | ICampaignCouponDAO | 	活动票券池DAO bean
campaignCouponItemDAO | ICampaignCouponItemDAO | 	活动票券DAO bean
campaignExecution | CampaignExecution | 	活动常用服务bean
campaignRequest | CampaignRequest | 	活动请求的参数
campaignEventCode | String | 活动事件CODE
clientId | Long | 客户ID
mobile | String | 手机号
serialNumber | String | 兑换码
campaignResponse | CampaignResponse | 活动响应
