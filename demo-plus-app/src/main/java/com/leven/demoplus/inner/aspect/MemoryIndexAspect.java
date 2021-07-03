package com.leven.demoplus.inner.aspect;//package com.leven.demoplus.inner.aspect;
//
//import io.micrometer.core.instrument.*;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Method;
//import java.time.Duration;
//import java.util.List;
//import java.util.concurrent.CompletionStage;
//
///**

// */
//@Aspect
//@Component("memoryIndexAspect")
//public class MemoryIndexAspect {
//
//    private final Timer.Builder timer = Timer.builder("memory.retrieval.time")
//            .description("memory index time monitor")
//            .publishPercentileHistogram()
//            .serviceLevelObjectives(
//                    Duration.ofMillis(5),
//                    Duration.ofMillis(10),
//                    Duration.ofMillis(20),
//                    Duration.ofMillis(40),
//                    Duration.ofMillis(60),
//                    Duration.ofMillis(80),
//                    Duration.ofMillis(100))
//            .minimumExpectedValue(Duration.ofMillis(5))
//            .maximumExpectedValue(Duration.ofMillis(5));
//
//
//    private final DistributionSummary.Builder summary = DistributionSummary.builder("memory.retrieval.docs").description("memory index docs");
//
//
//    private final MeterRegistry registry;
//
//    public MemoryIndexAspect(MeterRegistry registry) {
//        this.registry = registry;
//    }
//
//    @Pointcut(value = "execution(* com.heytap.ad.show.recall.core.service.AdRetrievalService.retrieve(com.heytap.ad.show.recall.core.context.RetrievalRequest,..)) && args(request,..)", argNames = "request")
//    public void retrieval(RetrievalRequest request) {
//    }
//
//    @Around(value = "com.heytap.ad.show.recall.core.aop.MemoryIndexAspect.retrieval(request)", argNames = "joinPoint,request")
//    public Object doRetrieval(ProceedingJoinPoint joinPoint, RetrievalRequest request) throws Throwable {
//        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
//        long timestamp = System.currentTimeMillis();
//        Object result = joinPoint.proceed();
//        if (CompletionStage.class.isAssignableFrom(method.getReturnType())) {
//            ((CompletionStage<?>) result).thenAccept(response -> this.record(request, (List<?>) response, timestamp));
//        } else {
//            this.record(request, (List<?>) result, timestamp);
//        }
//        return result;
//    }
//
//    private void record(RetrievalRequest request, List<?> result, long timestamp) {
//        Iterable<Tag> tags = this.tags(request);
//        this.timer.tags(tags).register(this.registry).record(Duration.ofMillis(System.currentTimeMillis() - timestamp));
//        this.summary.tags(tags).register(this.registry).record(result.size());
//    }
//
//    private Iterable<Tag> tags(RetrievalRequest request) {
//        String source = String.valueOf(RetrievalRequestUtils.originalPositionId(request));
//        String positionId = String.valueOf(RetrievalRequestUtils.positionId(request));
//        return Tags.of(
//                // 召回类型
//                "recallType", request.getRecallType().name(),
//                // 搜索类型
//                "searchType", String.valueOf(request.getSearchType().getNumber()),
//                // 旧广告位
//                "source", MetricsUtils.isSourceMetricEnabled(source) ? source : Common.Tag.UNKNOWN,
//                // 广告位
//                "position", MetricsUtils.isPositionMetricEnabled(positionId) ? positionId : Common.Tag.UNKNOWN,
//                // 流量号
//                "flowTag", MetricsUtils.isFlowMetricEnabled(request.getFlowTag()) ? request.getFlowTag() : Common.Tag.UNKNOWN
//        );
//    }
//}
