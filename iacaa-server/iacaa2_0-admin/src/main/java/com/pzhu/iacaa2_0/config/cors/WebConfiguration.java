package com.pzhu.iacaa2_0.config.cors;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * @author HuSen
 * @since 2020/9/24 7:31 下午
 */
@Configuration
public class WebConfiguration {


//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
//        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", buildConfig());
//        return new CorsFilter(urlBasedCorsConfigurationSource);
//    }
//
//    private CorsConfiguration buildConfig() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        // 允许任何域名
//        corsConfiguration.addAllowedOrigin("*");
//        // 允许任何头
//        corsConfiguration.addAllowedHeader("*");
//        // 允许任何方法
//        corsConfiguration.addAllowedMethod("*");
//        return corsConfiguration;
//    }
}
