package com.bankmandiri.streamfailedpe.validator;

import com.bankmandiri.streamfailedpe.controller.KafkaConsumerRegistryController;
import com.bankmandiri.streamfailedpe.model.ConsumerData;
import com.bankmandiri.streamfailedpe.model.Status;
import com.bankmandiri.streamfailedpe.model.StatusList;
import com.bankmandiri.streamfailedpe.utils.ErrorCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class RequestValidator {

	private static final Logger logger = LoggerFactory.getLogger(RequestValidator.class);

	public static Status validate(List<ConsumerData> req) {

		Status s = new Status();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		try {
			List<StatusList> stsList = new ArrayList<>();
			StatusList sts = new StatusList();
			if (req.isEmpty()) {
				sts.setResponses_code(ErrorCodeEnum.REQUEST_NOT_VALID.getCode());
				sts.setResponse_message(ErrorCodeEnum.REQUEST_NOT_VALID.getDefaultMsg());
				stsList.add(sts);
			}
			for (ConsumerData consumerData : req) {
				if (consumerData.getConsumerName().isEmpty() || consumerData.getConsumerTopic().isEmpty()) {
					sts.setResponses_code(ErrorCodeEnum.REQUEST_NOT_VALID.getCode());
					sts.setResponse_message(ErrorCodeEnum.REQUEST_NOT_VALID.getDefaultMsg());
					sts.setConsumerName(consumerData.getConsumerName());
					sts.setConsumerTopic(consumerData.getConsumerTopic());
					stsList.add(sts);
				}

				Pattern patternConsumerName = Pattern.compile("[a-z-]*");
				Matcher matcherConsumerName = patternConsumerName.matcher(consumerData.getConsumerName());

				if (!matcherConsumerName.matches()) {
					sts.setResponses_code(ErrorCodeEnum.CONSUMERNAME_NOT_VALID.getCode());
					sts.setResponse_message(ErrorCodeEnum.CONSUMERNAME_NOT_VALID.getDefaultMsg());
					sts.setConsumerName(consumerData.getConsumerName());
					sts.setConsumerTopic(consumerData.getConsumerTopic());
					stsList.add(sts);
				}

				Pattern patternConsumerTopic = Pattern.compile("[A-Z]*");
				Matcher matcherConsumerTopic = patternConsumerTopic.matcher(consumerData.getConsumerTopic());

				if (!matcherConsumerTopic.matches()) {
					sts.setResponses_code(ErrorCodeEnum.CONSUMERTOPIC_NOT_VALID.getCode());
					sts.setResponse_message(ErrorCodeEnum.CONSUMERTOPIC_NOT_VALID.getDefaultMsg());
					sts.setConsumerName(consumerData.getConsumerName());
					sts.setConsumerTopic(consumerData.getConsumerTopic());
					stsList.add(sts);
				}

				s.setResponse_list(stsList);
				s.setResponse_timestamp(sdf.format(new Date()));
			}

		}
		catch (Exception e) {
			logger.error("Error while validate request from user");
		}
		return s;
	}

}
