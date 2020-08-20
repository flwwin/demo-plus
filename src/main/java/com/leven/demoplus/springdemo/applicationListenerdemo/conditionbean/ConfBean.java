package com.leven.demoplus.springdemo.applicationListenerdemo.conditionbean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Component
//@Conditional()
@ConditionalOnExpression(value = "#{'1'.equals(environment['show.type'])||'2'.equals(environment['show.type'])}")
//@ConditionalOnProperty
public class ConfBean {
}
