package org.yangyuan.pay.core.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.yangyuan.pay.bean.AliTransferTrade;
import org.yangyuan.pay.bean.TransferStatus;
import org.yangyuan.pay.exception.TransferException;

import java.util.HashMap;
import java.util.Map;

/**
 * 转账定义支付宝抽象实现
 * @author yangyuan
 * @date 2019/1/4
 */
public abstract class AbstractAliTransfer extends AbstractAli implements Transfer<AliTransferTrade>{
    @Override
    public void transfer(AliTransferTrade transferTrade) throws TransferException {
        try {
            /*
                请求
             */
            JSONObject response = request(transferTrade, new AliTransferBizOptions() {
                @Override
                public Map<String, String> buildBizParams(AliTransferTrade aliTransferTrade) {
                    Map<String, String> params = new HashMap<>();

                    /*
                        必选项
                     */
                    if(StringUtils.isBlank(transferTrade.getOutBizNo())){
                        throw new RuntimeException("out_biz_no商户转账唯一订单号不能为空！");
                    }
                    if(StringUtils.isBlank(transferTrade.getPayeeType())){
                        throw new RuntimeException("payee_type收款方账户类型不能为空！");
                    }
                    if(StringUtils.isBlank(transferTrade.getPayeeAccount())){
                        throw new RuntimeException("payee_account收款方账户不能为空！");
                    }
                    if(StringUtils.isBlank(transferTrade.getAmount())){
                        throw new RuntimeException("amount转账金额不能为空！");
                    }
                    params.put("out_biz_no", transferTrade.getOutBizNo());
                    params.put("payee_type", transferTrade.getPayeeType());
                    params.put("payee_account", transferTrade.getPayeeAccount());
                    params.put("amount", transferTrade.getAmount());

                    /*
                        可选项
                     */
                    if(StringUtils.isNotBlank(transferTrade.getPayerShowName())){
                        params.put("payer_show_name", transferTrade.getPayerShowName());
                    }
                    if(StringUtils.isNotBlank(transferTrade.getPayeeRealName())){
                        params.put("payee_real_name", transferTrade.getPayeeRealName());
                    }
                    if(StringUtils.isNotBlank(transferTrade.getRemark())){
                        params.put("remark", transferTrade.getRemark());
                    }

                    return params;
                }

                @Override
                public String responseBizPropName() {
                    return "alipay_fund_trans_toaccount_transfer_response";
                }
            });

            /*
                业务状态解析
             */
            if(!requestSuccess(response)){
                throw new TransferException("支付宝转账失败。\n" + response.toJSONString());
            }
            if(StringUtils.isBlank(response.getString("order_id"))){
                throw new TransferException("支付宝转账失败(返回无效的order_id)。\n" + response.toJSONString());
            }
        }catch (Exception e){
            throw new TransferException(e);  //与支付宝通信失败则认为转账失败
        }
    }

    @Override
    public TransferStatus status(AliTransferTrade transferTrade) {
        try {
            /*
                请求
             */
            JSONObject response = request(transferTrade, new AliTransferBizOptions() {
                @Override
                public Map<String, String> buildBizParams(AliTransferTrade aliTransferTrade) {
                    Map<String, String> params = new HashMap<>();

                    /*
                        必选项
                     */
                    if(StringUtils.isBlank(transferTrade.getOutBizNo())){
                        throw new RuntimeException("out_biz_no商户转账唯一订单号不能为空！");
                    }
                    params.put("out_biz_no", transferTrade.getOutBizNo());

                    return params;
                }

                @Override
                public String responseBizPropName() {
                    return "alipay_fund_trans_order_query_response";
                }
            });

            /*
                业务状态解析
             */
            if(!requestSuccess(response)){
                return TransferStatus.unknownStatus(transferTrade.getOutBizNo());  //业务失败，认为转账状态未知
            }
            String status = response.getString("status");
            if("SUCCESS".equalsIgnoreCase(status)){  //转账成功
                return TransferStatus.successStatus(transferTrade.getOutBizNo());
            }
            if("FAIL".equalsIgnoreCase(status)){  //转账失败
                return TransferStatus.failStatus(transferTrade.getOutBizNo());
            }
            if("INIT".equalsIgnoreCase(status)){  //等待处理
                return TransferStatus.unknownStatus(transferTrade.getOutBizNo());
            }
            if("DEALING".equalsIgnoreCase(status)){  //处理中
                return TransferStatus.unknownStatus(transferTrade.getOutBizNo());
            }
            if("UNKNOWN".equalsIgnoreCase(status)){  //状态未知
                return TransferStatus.unknownStatus(transferTrade.getOutBizNo());
            }

            return TransferStatus.unknownStatus(transferTrade.getOutBizNo());  //其他状态认为转账状态未知
        }catch (Exception e){
            return TransferStatus.unknownStatus(transferTrade.getOutBizNo());  //与支付宝通信失败，认为转账状态未知，等待下次处理
        }
    }
}
