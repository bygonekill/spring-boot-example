package com.example.listener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.example.model.UserInfoModel;
import com.example.util.DateUtil;
import com.example.util.MqListenerHelperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 普通消息监听
 */
@Slf4j
@Component
public class NormalMsgListener implements MessageListener {

    @Override
    public Action consume(Message message, ConsumeContext context) {
        log.info("消息 id={},执行Host={}", message.getMsgID(), message.getBornHost());
        log.info("消息 Topic={},Tag={}", message.getTopic(), message.getTag());
        log.info("消息生成时间={}", DateUtil.formatDateMillisecond(new Date(message.getBornTimestamp())));
        log.info("消息执行次数={}", message.getReconsumeTimes());

        try {
            UserInfoModel userInfoModel = MqListenerHelperUtil.parseObjectMsg(message, UserInfoModel.class);
            log.info("消息内容={}", userInfoModel);

            //do something..
            return Action.CommitMessage;
        } catch (Exception e) {
            if (MqListenerHelperUtil.canRetryTimes(message.getReconsumeTimes())) {
                return Action.ReconsumeLater;
            }
            //消费失败
            return Action.ReconsumeLater;
        }
    }
    
}
